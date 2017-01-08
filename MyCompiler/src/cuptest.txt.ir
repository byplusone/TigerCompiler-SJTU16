# Before canonicalization: 
SEQ(
 SEQ(
  EXP(
   CONST 0),
  EXP(
   CONST 0)),
 EXP(
  CALL(
   NAME test,
    TEMP $fp)))
# After canonicalization: 
EXP(
 CALL(
  NAME test,
   TEMP $fp))
# Basic Blocks: 
LABEL L13
EXP(
 CALL(
  NAME test,
   TEMP $fp))
JUMP(
 NAME L12)
LABEL L12
# Trace Scheduled: 
LABEL L13
EXP(
 CALL(
  NAME test,
   TEMP $fp))
JUMP(
 NAME L12)
LABEL L12
# Instructions: 

#Begin to save calleeSaves!!
addi $sp,$sp,0
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

add $fp,$sp,40
L13:
addi $sp,$sp,-4
sw $fp,0($sp)
jal  test
addi $sp,$sp,4
j L12
L12:

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
addi $sp,$sp,0
#calleeSaves have been fetched!

jr $ra
# Before canonicalization: 
MOVE(
 TEMP $v0,
 ESEQ(
  SEQ(
   MOVE(
    TEMP t32,
    CALL(
     NAME allocRecord,
      CONST 0,
      CONST 8)),
   SEQ(
    MOVE(
     MEM(
      BINOP(PLUS,
       TEMP t32,
       CONST 0)),
     CONST 2),
    MOVE(
     MEM(
      BINOP(PLUS,
       TEMP t32,
       CONST 4)),
     CONST 2))),
  TEMP t32))
# After canonicalization: 
MOVE(
 TEMP t32,
 CALL(
  NAME allocRecord,
   CONST 0,
   CONST 8))
MOVE(
 MEM(
  BINOP(PLUS,
   TEMP t32,
   CONST 0)),
 CONST 2)
MOVE(
 MEM(
  BINOP(PLUS,
   TEMP t32,
   CONST 4)),
 CONST 2)
MOVE(
 TEMP $v0,
 TEMP t32)
# Basic Blocks: 
LABEL L15
MOVE(
 TEMP t32,
 CALL(
  NAME allocRecord,
   CONST 0,
   CONST 8))
MOVE(
 MEM(
  BINOP(PLUS,
   TEMP t32,
   CONST 0)),
 CONST 2)
MOVE(
 MEM(
  BINOP(PLUS,
   TEMP t32,
   CONST 4)),
 CONST 2)
MOVE(
 TEMP $v0,
 TEMP t32)
JUMP(
 NAME L14)
LABEL L14
# Trace Scheduled: 
LABEL L15
MOVE(
 TEMP t32,
 CALL(
  NAME allocRecord,
   CONST 0,
   CONST 8))
MOVE(
 MEM(
  BINOP(PLUS,
   TEMP t32,
   CONST 0)),
 CONST 2)
MOVE(
 MEM(
  BINOP(PLUS,
   TEMP t32,
   CONST 4)),
 CONST 2)
MOVE(
 TEMP $v0,
 TEMP t32)
JUMP(
 NAME L14)
LABEL L14
# Instructions: 

#Begin to save calleeSaves!!
addi $sp,$sp,0
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

add $fp,$sp,40
L15:
li t33,8
add $a0,t33,$zero
addi $sp,$sp,-4
li t34,0
sw t34,0($sp)
jal  allocRecord
addi $sp,$sp,4
add  t32, $v0,  $zero
addi t35,t32,0
li t38,2
add t36,t38, $zero
sw t36,0( t35)
addi t39,t32,4
li t42,2
add t40,t42, $zero
sw t40,0( t39)
add  $v0, t32,  $zero
j L14
L14:

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
addi $sp,$sp,0
#calleeSaves have been fetched!

jr $ra
