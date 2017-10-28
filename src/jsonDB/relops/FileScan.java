package jsonDB.relops;

import jsonDB.data.Row;
import jsonDB.validation.Table;
import static jsonDB.db.ValidationInterface.*;

import java.util.List;
import jsonDB.parser.JSON_RowIterator;
import jsonDB.parser.JSON_RowIterator.*;
import jsonDB.validation.Constraint;
import jsonDB.validation.ConstraintValidator;

import java.util.ArrayList;

public class FileScan extends Iterator{

	JSON_RowIterator jri;
	List<String> pointer_to_row; //row is List<String> form	
	Table table;	
	List<Constraint> table_constraints; 
	List<ConstraintValidator> table_constraint_validator; 


	/*
	 * Constructor for class FileScan. Given a Table, initialize the Iterator schema field.
	 * Create and open a new JSON_RowIterator() for the given Table data.
	 * 
	 * Initialize the state of the Iterator and the set of ConstraintValidators
	 */
	public FileScan(Table table)
	{
		//TODO
		jri = new JSON_RowIterator(table.getSchema().getName());
		this.table = table;		
		jri.open();		
		setSchema(this.table.getSchema());

		table_constraints = table.getConstraints();
		table_constraint_validator = new ArrayList<>();	

		for(int i = 0; i < table_constraints.size(); i++) {
			table_constraint_validator.add(table_constraints.get(i).getConstraintValidator(table.getSchema()));		
		}
	}

	public void close() {
		//TODO

		//check if it's just making it null!
		jri.close();
		jri = null;
		pointer_to_row = null;
	}
	
	public boolean isOpen() {
		//TODO
		if(jri == null) return false;
		return true;
	}

	//reset is a way of opening the thing again?
	public void reset() {
		//TODO
		jri = new JSON_RowIterator(table.getSchema().getName());
			
	}

	/*
	 * (non-Javadoc)
	 * @see jsonDB.relops.Iterator#hasNext()
	 * 
	 * Compute the next valid row read from the underlying JSON_RowIterator on the table data.
	 * A row should be validated on schema as well as on whether it satisfies the constraints of the
	 * table. If a row is not valid, it should be skipped by the Iterator and the hasNext() function
	 * should continue attempting to find the next valid row.
	 * 
	 * Return true if a valid row has been found, else return false.
	 */
	public boolean hasNext() throws IllegalStateException, IllegalArgumentException {
		//TODO
	
		//check the current row to see if it is valid using the 
		while (true) {
			if(!jri.hasNext()) return false;
			
			int error_count = 0;
			pointer_to_row = jri.getNext();
						
			Row row;

			try {
				row = readAndValidateRow(table.getSchema(), pointer_to_row);
			} catch	(IllegalArgumentException e) {
				continue;
			}	
		if(!validateConstraints(table_constraint_validator, row)) {
				error_count++;
			}
			
			if (error_count == 0) {
				break;			
			}
				
		}
		
		return true;
	}

	public static boolean validateConstraints(List<ConstraintValidator> cvs, Row r)
	{

		for(ConstraintValidator cv : cvs)
		{
			if(!cv.validateConstraint(r))
				return false;
		}
		return true;
	}


	public Row getNext() {
		//TODO

		return readAndValidateRow(table.getSchema(), pointer_to_row);
	}
}
