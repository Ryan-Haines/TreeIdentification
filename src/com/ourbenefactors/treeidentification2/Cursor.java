
package com.ourbenefactors.treeidentification2;

public class Cursor{
   	private Number currentPageIndex;
   	private String estimatedResultCount;
   	private String moreResultsUrl;
   	private Pages[] pages;
   	private String resultCount;
   	private String searchResultTime;

 	public Number getCurrentPageIndex(){
		return this.currentPageIndex;
	}
	public void setCurrentPageIndex(Number currentPageIndex){
		this.currentPageIndex = currentPageIndex;
	}
 	public String getEstimatedResultCount(){
		return this.estimatedResultCount;
	}
	public void setEstimatedResultCount(String estimatedResultCount){
		this.estimatedResultCount = estimatedResultCount;
	}
 	public String getMoreResultsUrl(){
		return this.moreResultsUrl;
	}
	public void setMoreResultsUrl(String moreResultsUrl){
		this.moreResultsUrl = moreResultsUrl;
	}
 	public Pages[] getPages(){
		return this.pages;
	}
	public void setPages(Pages[] pages){
		this.pages = pages;
	}
 	public String getResultCount(){
		return this.resultCount;
	}
	public void setResultCount(String resultCount){
		this.resultCount = resultCount;
	}
 	public String getSearchResultTime(){
		return this.searchResultTime;
	}
	public void setSearchResultTime(String searchResultTime){
		this.searchResultTime = searchResultTime;
	}
}
