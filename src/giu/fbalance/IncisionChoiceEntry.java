package giu.fbalance;

public class IncisionChoiceEntry {
	 
    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private String title;
    private String description;
 
    public IncisionChoiceEntry(String title, String description) {
        super();
        this.title = title;
        this.description = description;
    }
}