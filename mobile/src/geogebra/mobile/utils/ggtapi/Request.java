package geogebra.mobile.utils.ggtapi;

/**
 * Request POJO
 * 
 * @author Matthias Meisinger
 */
class Request
{
	enum Task
	{
		fetch;
	}

	public enum Order
	{
		id, title, type, description, timestamp, author, language, featured, likes, relevance;
	}

	public enum Type
	{
		asc, desc;
	}

	public enum Filters
	{
		id, title, search, type, description, timestamp, author, language, featured, likes;
	}

	private static final String api = "1.0.0";

	private Fields[] fields = Fields.values();
	private Filters[] filters;
	private String query = "";

	private Order by;
	private Type type;
	private int limit = GeoGebraTubeAPI.STANDARD_RESULT_QUANTITY;

	/**
	 * Constructor for a Featured Materials Request
	 */
	public Request()
	{
		this.filters = new Filters[] { Filters.featured };
		this.by = Order.likes;
		this.type = Type.desc;
	}

	/**
	 * Constructor for a Search Request
	 * 
	 * @param String
	 *          query
	 */
	public Request(String query)
	{
		this.filters = new Filters[] { Filters.search };
		this.query = query;
	}

	/**
	 * Constructor for a Request by Filters
	 * 
	 * @param filters
	 * @param by
	 */
	public Request(Filters[] filters, Order by, Type sort)
	{
		this.filters = filters;
		this.by = by;
		this.type = sort;
	}

	public static String getApi()
  {
  	return api;
  }

	public Fields[] getFields()
  {
  	return fields;
  }

	public Filters[] getFilters()
  {
  	return filters;
  }

	public String getQuery()
  {
  	return query;
  }

	public Order getBy()
  {
  	return by;
  }

	public Type getType()
  {
  	return type;
  }

	public int getLimit()
  {
  	return limit;
  }
}
