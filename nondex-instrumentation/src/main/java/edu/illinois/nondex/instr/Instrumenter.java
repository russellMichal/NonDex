/*
The MIT License (MIT)
Copyright (c) 2015 Alex Gyori
Copyright (c) 2015 Owolabi Legunsen
Copyright (c) 2015 Darko Marinov
Copyright (c) 2015 August Shi


Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package edu.illinois.nondex.instr;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;


public final class Instrumenter {
    private static final HashSet<String> classesToShuffle = new HashSet<>();

    static {
        classesToShuffle.add("java/lang/Class.class");
        classesToShuffle.add("java/lang/reflect/Field.class");
        classesToShuffle.add("java/io/File.class");
    }

    private Instrumenter() {
    }

    private static ClassVisitor createHashMapShuffler(CheckClassAdapter ca) {
        return new HashMapShufflingAdder(ca);
    }

    private static ClassVisitor createConcurrentHashMapShuffler(CheckClassAdapter ca) {
        return new ConcurrentHashMapShufflingAdder(ca);
    }

    private static void instrumentClass(String className,
                                        Function<CheckClassAdapter, ClassVisitor> createShuffler,
                                        ZipFile rt, ZipOutputStream outZip) throws IOException {

        InputStream classStream = rt.getInputStream(rt.getEntry(className));
        ClassReader cr = new ClassReader(classStream);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        CheckClassAdapter ca = new CheckClassAdapter(cw);
        ClassVisitor cv = createShuffler.apply(ca);

        cr.accept(cv, 0);
        byte[] hasharr = cw.toByteArray();

        ZipEntry zipEntry = new ZipEntry(className);
        outZip.putNextEntry(zipEntry);
        outZip.write(hasharr, 0, hasharr.length);
        outZip.closeEntry();
    }

    public static final void instrument(String rtjar, String outjar) throws IOException {
        ZipFile rt = new ZipFile(rtjar);
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(outjar));

        for (String cl : classesToShuffle) {
            InputStream clInputStream = rt.getInputStream(rt.getEntry(cl));

            ClassReader cr = new ClassReader(clInputStream);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

            ClassVisitor cv = new AddShufflingToClassVisitor(cw);

            cr.accept(cv, 0);

            byte[] arr = cw.toByteArray();

            ZipEntry entry = new ZipEntry(cl);
            outZip.putNextEntry(entry);
            outZip.write(arr, 0, arr.length);
            outZip.closeEntry();
        }

        HashIteratorShufflerASMDump hashIterShuffDump = new HashIteratorShufflerASMDump();
        ZipEntry hashIterShuffEntry = new ZipEntry("java/util/HashIteratorShuffler.class");
        outZip.putNextEntry(hashIterShuffEntry);
        byte[] hashIterShuffBytes = hashIterShuffDump.dump();
        outZip.write(hashIterShuffBytes, 0, hashIterShuffBytes.length);
        outZip.closeEntry();

        instrumentClass("java/util/HashMap$HashIterator.class", Instrumenter::createHashMapShuffler, rt, outZip);
        instrumentClass("java/util/concurrent/ConcurrentHashMap$Traverser.class",
                Instrumenter::createConcurrentHashMapShuffler, rt, outZip);

        outZip.close();
    }
}
