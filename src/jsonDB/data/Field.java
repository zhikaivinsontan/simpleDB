package jsonDB.data;

public class Field {
	private String fieldName;
	private DataType type;
	
	/* Field Constructors */
	public Field()
	{
		
	}
	
	public Field(String fName, DataType t)
	{
		fieldName = fName;
		type = t;
	}
	
	/* Getters and setters */
	public String getFieldName()
	{
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}
	
	
}
