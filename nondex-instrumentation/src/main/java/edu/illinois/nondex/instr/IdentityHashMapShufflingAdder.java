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

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class IdentityHashMapShufflingAdder extends ClassVisitor {
    private class MethodProperties {
        private int access;
        private String name;
        private String desc;
        private String signature;
        private String[] exceptions;
    }

    private MethodProperties hasNextProp;
    private MethodProperties nextIndexProp;


    public IdentityHashMapShufflingAdder(ClassVisitor ca) {
        super(Opcodes.ASM5, ca);

        this.hasNextProp = new MethodProperties();
        this.nextIndexProp = new MethodProperties();
    }

    public void addIndicies() {
        FieldVisitor fv = super.visitField(0, "indicies", "Ljava/util/List;",
                "Ljava/util/List<Ljava/lang/Integer;>;", null);
        fv.visitEnd();
    }

    public void addIter() {
        FieldVisitor fv = super.visitField(0, "iter", "Ljava/util/Iterator;",
                "Ljava/util/Iterator<Ljava/lang/Integer;>;", null);
        fv.visitEnd();
    }

    public void addNextIndex() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PROTECTED, "nextIndex", "()I", null, null);
        /*MethodVisitor mv = super.visitMethod(nextIndexProp.access, nextIndexProp.name, nextIndexProp.desc,
                nextIndexProp.signature, nextIndexProp.exceptions);*/
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator", "iter",
                "Ljava/util/Iterator;");
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
        mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    public void addHasNext() {
        /*MethodVisitor mv = super.visitMethod(hasNextProp.access, hasNextProp.name, hasNextProp.desc,
                hasNextProp.signature, hasNextProp.exceptions);*/
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "hasNext", "()Z", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator", "iter",
                "Ljava/util/Iterator;");
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    public void addInit() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "<init>",
                "(Ljava/util/IdentityHashMap;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                "this$0", "Ljava/util/IdentityHashMap;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                "this$0", "Ljava/util/IdentityHashMap;");
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap", "size", "I");
        Label l0 = new Label();
        mv.visitJumpInsn(Opcodes.IFEQ, l0);
        mv.visitInsn(Opcodes.ICONST_0);
        Label l1 = new Label();
        mv.visitJumpInsn(Opcodes.GOTO, l1);
        mv.visitLabel(l0);
        mv.visitFrame(Opcodes.F_FULL, 2, new Object[]{
            "java/util/IdentityHashMap$IdentityHashMapIterator", "java/util/IdentityHashMap"},
                1, new Object[]{
                    "java/util/IdentityHashMap$IdentityHashMapIterator"});
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                "this$0", "Ljava/util/IdentityHashMap;");
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap", "table", "[Ljava/lang/Object;");
        mv.visitInsn(Opcodes.ARRAYLENGTH);
        mv.visitLabel(l1);
        mv.visitFrame(Opcodes.F_FULL, 2, new Object[]{
            "java/util/IdentityHashMap$IdentityHashMapIterator", "java/util/IdentityHashMap"},
                2, new Object[]{
                    "java/util/IdentityHashMap$IdentityHashMapIterator", Opcodes.INTEGER});
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator", "index", "I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator", "this$0",
                "Ljava/util/IdentityHashMap;");
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap", "modCount", "I");
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                "expectedModCount", "I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ICONST_M1);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                "lastReturnedIndex", "I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                "this$0", "Ljava/util/IdentityHashMap;");
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap", "table", "[Ljava/lang/Object;");
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                "traversalTable", "[Ljava/lang/Object;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                "indicies", "Ljava/util/List;");
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/IdentityHashMap$IdentityHashMapIterator",
                "original_hasNext", "()Z", false);
        Label l3 = new Label();
        mv.visitJumpInsn(Opcodes.IFEQ, l3);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                "indicies", "Ljava/util/List;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/IdentityHashMap$IdentityHashMapIterator",
                "original_nextIndex", "()I", false);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
        mv.visitInsn(Opcodes.POP);
        mv.visitJumpInsn(Opcodes.GOTO, l2);
        mv.visitLabel(l3);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                "indicies", "Ljava/util/List;");
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/illinois/nondex/shuffling/ControlNondeterminism",
                "shuffle", "(Ljava/util/List;)Ljava/util/List;", false);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                "indicies", "Ljava/util/List;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                "indicies", "Ljava/util/List;");
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "java/util/IdentityHashMap$IdentityHashMapIterator",
                "iter", "Ljava/util/Iterator;");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(3, 2);
        mv.visitEnd();
    }

    @Override
    public void visitEnd() {
        addNextIndex();

        addHasNext();

        addInit();

        addIndicies();

        addIter();

        super.visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        if ("hasNext".equals(name)) {
            hasNextProp.access = access;
            hasNextProp.name = name;
            hasNextProp.desc = desc;
            hasNextProp.signature = signature;
            hasNextProp.exceptions = exceptions;

            return super.visitMethod(access, "original_hasNext", desc, signature, exceptions);
        }
        if ("nextIndex".equals(name)) {
            nextIndexProp.access = access;
            nextIndexProp.name = name;
            nextIndexProp.desc = desc;
            nextIndexProp.signature = signature;
            nextIndexProp.exceptions = exceptions;

            return super.visitMethod(access, "original_nextIndex", desc, signature, exceptions);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
