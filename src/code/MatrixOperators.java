package code;

public class MatrixOperators implements  Operators {
public MatrixOperator getUp() {
	return MatrixOperator.up;
}
public MatrixOperator getDown() {
	return MatrixOperator.down;
}
public MatrixOperator getRight() {
	return MatrixOperator.right;
}
public MatrixOperator getLeft() {
	return MatrixOperator.left;
}
public MatrixOperator getFly() {
	return MatrixOperator.fly;
}
public MatrixOperator getTakePill() {
	return MatrixOperator.takePill;
}
public MatrixOperator getKill() {
	return MatrixOperator.kill;
}
public MatrixOperator getCarry() {
	return MatrixOperator.carry;
}
public MatrixOperator getDrop() {
	return MatrixOperator.drop;
}
}
