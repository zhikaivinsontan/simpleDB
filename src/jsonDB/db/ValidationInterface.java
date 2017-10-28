package jsonDB.db;

import java.util.List;
import java.util.Map;

import jsonDB.data.Predicate;
import jsonDB.data.Row;
import jsonDB.validation.ConstraintValidator;
import jsonDB.validation.Query;
import jsonDB.validation.Schema;
import jsonDB.validation.Table;
import jsonDB.validation.User;

import java.util.HashMap;
import static jsonDB.parser.Parser.*;

import jsonDB.data.Field;
import jsonDB.parser.JSON_RowIterator;
import java.util.ArrayList;

import jsonDB.validation.Constraint;
import jsonDB.validation.Constraint.ConstraintType;

public class ValidationInterface {

	/*
	 * Given a list of Table Names, use the Parser to read them into memory. The result
	 * should be a Map{Table Name => Table}. You may assume that a table name uniquely identifies
	 * a single table - there will be no duplicate names.
	 */
	public static Map<String, Table> readTables(List<String> tableNames) {
		//TODO
		/*		
		System.out.println("TEST");
		
		for(int i = 0; i < tableNames.size(); i++) {
			System.out.println(tableNames.get(i));		
		}
		*/
		
		Map<String, Table> tables_map = new HashMap<String, Table>();
		for(int i = 0; i < tableNames.size(); i++) {;	
			tables_map.put(tableNames.get(i), parseTable(tableNames.get(i)));	
		}
		 
		return tables_map;
	}

	/*
	 * Given the name of a file containing the User information, use the Parser to read them into
	 * memory. Invalid Users should be discarded. The result should be a Map{User Name => User}.
	 * 
	 * A User is considered to be invalid if either have a subschema defined for a table that
	 * does not exist in the database, or if they have fields listed in a subschema that are not
	 * part of the table for that subschema.
	 * 
	 * You may assume that a user name uniquely identifies a single user - there will be no duplicates.
	 */
	public static Map<String, User> readAndValidateUsers(String userFile) {
		//TODO
		Map<String, User> users_map = new HashMap<String, User>();
		List<User> list = parseUsers(userFile);
		
		for (int i = 0; i < list.size(); i++) {
			
			User current_user = list.get(i);
								
			//get the keys of the subschemas of the current user we are evaluating
			Map<String, List<String>> subSchema_map = current_user.getSubSchemas();						
			Boolean error = false;			
			for ( String key : subSchema_map.keySet() ) {
				//check if there is a table with the key
				if (SimpleDB.db.getTable(key) != null) {

					//if there is, check the particular subschema's fields
					error = validateFields(subSchema_map.get(key),SimpleDB.db.getTable(key));
							
				} else {
					error = true;
				}	
			}
			
			if (error == false) {
				//System.out.println("verified " + current_user.getUserName());
				users_map.put(current_user.getUserName(), current_user);
			}
		}

		return users_map;
	}

	public static boolean validateFields(List<String> SubSchema, Table table) {
		
		List<Field> strawberry_field = table.getSchema().getFields();
		for(int i = 0; i < SubSchema.size(); i++) {
			String current_ss_field = SubSchema.get(i);
			Boolean found = false;			
			for (int j = 0; j < strawberry_field.size(); j++) {
				String current_table_field = strawberry_field.get(j).getFieldName();
				//System.out.println(current_table_field);				
				if (current_table_field.equals(current_ss_field)) {
					found = true;
					//System.out.println("FOUND! " + current_ss_field);
					break;				
				} 
			}

			if (found == false) return true; 	
		}

		return false;
	}


	/*
	 * Given a Schema and the raw row data for a Row (given as a List of Strings, provided from the
	 * JSON_RowIterator.getNext() method call), validate the given row data on the schema.
	 * If the row data is valid, create and return a Row object of that data (make sure you 
	 * cast each value to the proper type). If the Row is invalid, throw an IllegalArgumentException.
	 * 
	 * A Row is valid iff the data types of all of its values match the data types defined in the
	 * Schema. The row must include exactly the same number of fields as the Schema, and the order
	 * of the values cannot be changed.
	 * 
	 * REMEMBER TO HANDLE NULL VALUES - as long as the constraints allow NULL values the Row should
	 * be accepted as valid, but remember that you have to handle these values when converting to
	 * the Row. The provided raw row_data from the Parser will store these as the null value - it
	 * will not be a String representation. The returned Row object should also store the null data
	 * as the null value.
	 * 
	 * For example, for the Schema:
	 * Student(puid INTEGER, name STRING, gpa FLOAT)
	 * 
	 * Valid row: ["4", "VALID", "3.7"]
	 * Invalid row: ["3.2", "INVALID", "4"]
	 */
	public static Row readAndValidateRow(Schema s, List<String> row_data) throws IllegalArgumentException {
		// TODO
		int num_of_fields = s.getFields().size();
		List<Field> s_fields = s.getFields();
		if (row_data.size() != num_of_fields) { 
			throw new IllegalArgumentException(); 
		}

		List<Object> data_row = new ArrayList<Object>();
		for (int i = 0; i < s_fields.size(); i++) {
			String s_i_type = s_fields.get(i).getType().toString();	
			if (row_data.get(i) != null && !checkDataType(row_data.get(i),s_i_type)) {				
				throw new IllegalArgumentException(); 
			} else if (row_data.get(i) == null){
				data_row.add(null);
			} else {		
				if (s_i_type.equals("INTEGER")) {
					data_row.add(Integer.parseInt(row_data.get(i)));
				} else if(s_i_type.equals("LONG")) {
					data_row.add(Long.parseLong(row_data.get(i)));
				} else if(s_i_type.equals("FLOAT")) {
					data_row.add(Float.parseFloat(row_data.get(i)));
				} else if(s_i_type.equals("DOUBLE")) {
					data_row.add(Double.parseDouble(row_data.get(i)));
				} else if(s_i_type.equals("BOOLEAN")) {
					data_row.add(Boolean.parseBoolean(row_data.get(i)));
				} else {
					data_row.add(row_data.get(i));
				}	
			}		
		}
		
		Row row = new Row();
		row.setValues(data_row);	
		return row;
	
	}
	

	//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!CHECK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!CHECK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public static boolean checkDataType(String data, String attributeType) {
		
		if (attributeType.equals("INTEGER")) {
			try {
				int ret = Integer.parseInt(data);
				return true;
			} catch(Exception e) {
				return false;
			}
			
		}

		if(attributeType.equals("LONG")) {
			try {
				long ret = Long.parseLong(data);
				return true;
			} catch(Exception e) {
				return false;
			}
			
		}

		if(attributeType.equals("FLOAT")) {
			try {
				float ret = Float.parseFloat(data);
				return true;
			} catch(Exception e) {
				return false;
			}
			
		}
		
		if(attributeType.equals("DOUBLE")) {
			try {
				double ret = Double.parseDouble(data);
				return true;
			} catch(Exception e) {
				return false;
			}
			
		}

		if(attributeType.equals("BOOLEAN")) {
			try {
				boolean ret = Boolean.parseBoolean(data);
				return true;
			} catch(Exception e) {
				return false;
			}
			
		}

			return true;
	}


	

	/*
	 * Given a List of ConstraintValidator objects and a Row, validate the Row against all of the
	 * constraints. Return true if the row satisfies all constraints, false otherwise.
	 */
	public static boolean validateConstraints(List<ConstraintValidator> cvs, Row r)
	{

		for(ConstraintValidator cv : cvs)
		{
			if(!cv.validateConstraint(r))
				return false;
		}
		return true;
	}

	/*
	 * Given a Table, use the JSON_RowIterator to iterate through the row data. Validate each row
	 * based on the Schema and constraints - the row must be valid on the Schema of the given Table,
	 * and must satisfy all constraints that exist on the Table.
	 * 
	 * For every invalid row, print an error message to System.out specifying whether the error was
	 * due to the row being invalid for the Schema or invalid on the constraints of the Table.
	 * 
	 * Return true if all data is valid - otherwise return false.
	 */
	public static boolean validateData(Table table) {
		//TODO

		Schema schema = table.getSchema();
		JSON_RowIterator jri = new JSON_RowIterator(table.getSchema().getName());
		jri.open();


		List<Constraint> table_constraints = table.getConstraints();
		List<ConstraintValidator> table_constraint_validator = new ArrayList<>();	
		
		//add everything except unique in
		for(int i = 0; i < table_constraints.size(); i++) {

			table_constraint_validator.add(table_constraints.get(i).getConstraintValidator(table.getSchema()));		
		}
		
		boolean valid = true;
		
		while(jri.hasNext()) {
					
			Row current_row = null;		
			try {
				current_row = readAndValidateRow(table.getSchema(), jri.getNext());

				if(!validateConstraints(table_constraint_validator, current_row)) {
					System.out.println("Invalid: Row has Constraint Error"); //keep this!
					valid = false;
				}


			} catch(Exception e) {
				System.out.println("Invalid: Row has Schema Error");	//keep this!
				valid = false;
			}
			

		}
		
		jri.close();
		
		return valid;
	}

	/*
	 * Given a Query, validate the User's access rights and whether the fields/schemas accessed
	 * in the Query are valid.
	 * 
	 * A Query is valid if the User has permission to access all fields accessed by the query (i.e.
	 * those schemas/fields are present in the User's set of subschemas) and if the schemas/fields
	 * accessed by the query actually exist in the database.
	 * 
	 * Return true if the Query is valid, false otherwise.
	 */
	public static boolean validateQuery(Query q) {
		//TODO
		String user_name = q.getUserName();
		User user;
		//check if name of user is even in the map
			
		user = SimpleDB.db.getUser(user_name);
		if(user == null) return false;		

		//contains schema and fields user can access		
		Map<String, List<String>> user_schemas_map = user.getSubSchemas();
		
		//assuming that the user can only query on one table!!?!?!?!?!?!!??!		
		String query_table = q.getFromTable();
		
		//check schema rights of the user - if user has rights to use the schema
		if(user_schemas_map.get(query_table) == null) return false;		


		List<String> query_Fields = q.getSelectFields();
		List<String> user_Fields = user_schemas_map.get(query_table);
		
		//checks each query's schema's fields against the perimission of the user that is in the user object
		for (int i = 0; i < query_Fields.size(); i++){
			if(!user_Fields.contains(query_Fields.get(i))) {
				return false;
			}
		}

		//check the predicate fields
		List<List<Predicate>> predicate_fields = q.getWherePredicates();

		Table query_table_real = SimpleDB.db.getTable(query_table);
		Schema query_schema = query_table_real.getSchema();

		for(int i = 0; i < predicate_fields.size(); i++) {
			for (int j = 0; j < predicate_fields.get(i).size(); j++) {

				String pred_field_name = predicate_fields.get(i).get(j).getFieldName();
				if(query_schema.getColumnNumber(pred_field_name) == -1) {
					return false;
				}
			}
		}
		return true;
	}
}
