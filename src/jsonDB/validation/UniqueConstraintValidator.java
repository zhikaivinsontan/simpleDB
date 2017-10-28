package jsonDB.validation;

import jsonDB.data.Row;

import java.util.Map;
import java.util.HashMap;


public class UniqueConstraintValidator implements ConstraintValidator{
	

	private static Map<Object, Row> myMap;
	int column_number;	

	/*
	 * Constructor for class UniqueConstraintValidator, given the Constraint and the
	 * Schema the constraint applies on. Initializes ConstraintValidator state.
	 */
	public UniqueConstraintValidator(Constraint constraint, Schema s) throws IllegalConstraintException {
		//TODO
		myMap = new HashMap<>();

		String fieldName = constraint.getFieldName();
		if (s.getColumnNumber(fieldName) == -1) {
			throw new IllegalConstraintException("FieldName not Found");
		}

		column_number =	s.getColumnNumber(fieldName);	


	}
	
	/*
	 * Checks to see if the given Row has a unique value in the field specified by the Constraint.
	 * Return true if this value is unique, false otherwise.
	 * 
	 * This is calculated lazily, and the first occurence of a value will always be considered valid
	 * even if a duplicate value appears later. Only subsequent non-unique values should be considered
	 * to violate the constraint.
	 * 
	 * For example, given two Rows received in the following order:
	 * [1, "ABC"]
	 * [2, "ABC"]
	 * and a UNIQUE constraint on the second field, Row 1 would be accepted as valid, while Row 2
	 * would not.
	 */
	public boolean validateConstraint(Row r) {
		//TODO

		if(r == null) {System.out.println("GOOD GAME" + column_number);}

		Object key = r.getColumn(column_number);
		if(myMap.get(key) != null) {
			return false;
		} else {
			myMap.put(key,r);
			return true;
		}

	}

}
