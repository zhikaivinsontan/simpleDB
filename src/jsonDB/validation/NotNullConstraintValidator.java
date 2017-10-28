package jsonDB.validation;

import jsonDB.data.Row;

public class NotNullConstraintValidator implements ConstraintValidator{

	int column_number;
	
	/*
	 * Constructor for class NotNull, given the Constraint and the
	 * Schema the constraint applies on. Initializes ConstraintValidator state.
	 */
	public NotNullConstraintValidator(Constraint constraint, Schema s) throws IllegalConstraintException {
		//TODO
		//check if column that constraint is applied on appears in the schema
		String fieldName = constraint.getFieldName();
		if (s.getColumnNumber(fieldName) == -1) {
			throw new IllegalConstraintException("FieldName not Found");
		}

		column_number =	s.getColumnNumber(fieldName);			
	}	
	
	/*
	 * Checks to see if the specified field in the given Row is not NULL. Returns true if
	 * it is not null, false otherwise.
	 * 
	 */
	public boolean validateConstraint(Row r) {
		//TODO
	
		if(r.getColumn(column_number) == null) {
			return false;
		}	

		return true;
	}

}
