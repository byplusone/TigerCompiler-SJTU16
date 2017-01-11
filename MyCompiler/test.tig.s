# Before canonicalization: 
EXP(
 ESEQ(
  SEQ(
   MOVE(
    MEM(
     BINOP(PLUS,
      TEMP $fp,
      CONST -4)),
    CONST 1),
   MOVE(
    MEM(
     BINOP(PLUS,
      TEMP $fp,
      CONST -8)),
    CONST 2)),
  BINOP(PLUS,
   MEM(
    BINOP(PLUS,
     TEMP $fp,
     CONST -4)),
   MEM(
    BINOP(PLUS,
     TEMP $fp,
     CONST -8)))))
# After canonicalization: MOVE(
 MEM(
  BINOP(PLUS,
   TEMP $fp,
   CONST -4)),
 CONST 1)
MOVE(
 MEM(
  BINOP(PLUS,
   TEMP $fp,
   CONST -8)),
 CONST 2)
EXP(
 BINOP(PLUS,
  MEM(
   BINOP(PLUS,
    TEMP $fp,
    CONST -4)),
  MEM(
   BINOP(PLUS,
    TEMP $fp,
    CONST -8))))
# Basic Blocks: 
#
LABEL L1
MOVE(
 MEM(
  BINOP(PLUS,
   TEMP $fp,
   CONST -4)),
 CONST 1)
MOVE(
 MEM(
  BINOP(PLUS,
   TEMP $fp,
   CONST -8)),
 CONST 2)
EXP(
 BINOP(PLUS,
  MEM(
   BINOP(PLUS,
    TEMP $fp,
    CONST -4)),
  MEM(
   BINOP(PLUS,
    TEMP $fp,
    CONST -8))))
JUMP(
 NAME L0)
LABEL L0
# Trace Scheduled: 
LABEL L1
MOVE(
 MEM(
  BINOP(PLUS,
   TEMP $fp,
   CONST -4)),
 CONST 1)
MOVE(
 MEM(
  BINOP(PLUS,
   TEMP $fp,
   CONST -8)),
 CONST 2)
EXP(
 BINOP(PLUS,
  MEM(
   BINOP(PLUS,
    TEMP $fp,
    CONST -4)),
  MEM(
   BINOP(PLUS,
    TEMP $fp,
    CONST -8))))
JUMP(
 NAME L0)
LABEL L0
# Instructions: 

#Begin to save calleeSaves!!
addi $sp,$sp,-8
sw $s0,-4($sp)
sw $s1,-8($sp)
sw $s2,-12($sp)
sw $s3,-16($sp)
sw $s4,-20($sp)
sw $s5,-24($sp)
sw $s6,-28($sp)
sw $s7,-32($sp)
sw $fp,-36($sp)
sw $ra,-40($sp)
addi $sp,$sp,-40
#calleeSaves have been saved!

add $fp,$sp,48
L1:
addi t32,$fp,-4
li t35,1
add t33,t35, $zero
sw t33,0( t32)
addi t36,$fp,-8
li t39,2
add t37,t39, $zero
sw t37,0( t36)
lw t41,-4($fp)
lw t42,-8($fp)
add t40,t41,t42
j L0
L0:

#Begin to fetch calleeSaves!
addi $sp,$sp,40
lw $s0,-4($sp)
lw $s1,-8($sp)
lw $s2,-12($sp)
lw $s3,-16($sp)
lw $s4,-20($sp)
lw $s5,-24($sp)
lw $s6,-28($sp)
lw $s7,-32($sp)
lw $fp,-36($sp)
lw $ra,-40($sp)
addi $sp,$sp,8
#calleeSaves have been fetched!

jr $ra
