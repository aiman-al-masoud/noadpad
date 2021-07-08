package com.luxlunaris.noadpadlight.control.classes;

import com.luxlunaris.noadpadlight.control.interfaces.PageListener;
import com.luxlunaris.noadpadlight.control.interfaces.Pageable;
import com.luxlunaris.noadpadlight.model.classes.SinglePage;
import com.luxlunaris.noadpadlight.model.classes.comparators.LastModifiedComparator;
import com.luxlunaris.noadpadlight.model.interfaces.Page;
import com.luxlunaris.noadpadlight.ui.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is a facade controller that maintains a list of all of the user's pages,
 * and provides methods to create a new page, get a batch of pages of a specified size,
 * and more...
 *
 */
public class Notebook implements Pageable, PageListener {


	/**
	 * The instance of this Singleton class
	 */
	static Notebook instance;


	/**
	 * The path to which all of the pages are stored
	 */
	private static String PAGES_DIR = Paths.APP_DIR_PATH+File.separator+"pages";

	/**
	 * List of pages loaded in memory
	 */
	ArrayList<Page> pagesList;

	/**
	 * Current page index
	 */
	int currentPage;


	private Notebook() {
		pagesList = new ArrayList<Page>();
		loadPages();
		currentPage = 0;
	}

	/**
	 * Notebook is a Singleton
	 * @return
	 */
	public static Notebook getInstance() {
		return instance!=null? instance : (instance = new Notebook());		
	}


	/**
	 * Create and return a new page with a default name: the unix-time of is creation
	 * @return
	 */
	public Page newPage(){
		return newPage(System.currentTimeMillis()+"");
	}


	/**
	 * Create a new page and return it
	 * @param name
	 * @return
	 */
	private Page newPage(String name) {
		SinglePage page = new SinglePage(PAGES_DIR+File.separator+name);
		if(!page.exists()) {
			page.create();
			pagesList.add(page);
		}
		return (Page)page;
	}




	/**
	 * When a page is deleted, it informs the Notebook
	 * @param page
	 */
	@Override
	public void onDeleted(Page page) {
		pagesList.remove(page);
	}


	@Override
	public Page[] getNext(int amount) {
		List<Page> result = pagesList.subList(Math.max(currentPage, 0), Math.min(currentPage+amount, pagesList.size()));
		currentPage += result.size()==0? 0 :  amount;
		return result.toArray(new Page[0]);
	}

	@Override
	public Page[] getPrevious(int amount) {
		List<Page> result = pagesList.subList(Math.max(currentPage-amount, 0),  Math.max(0, Math.min(currentPage, pagesList.size()-1))  );
		currentPage-= result.size()==0? 0 :  amount;
		return result.toArray(new Page[0]);
	}
	
	
	/**
	 * Get an array of pages by whitespace-separated keywords.
	 * @param keywords
	 * @return
	 */
	public Page[] getByKeywords(String keywords) {
		
		String[] keywordz = keywords.split("\\s+");
		ArrayList<Page> result = new ArrayList<>(pagesList);
				
		for(Page page : pagesList) {
			for(String keyword : keywordz) {
				boolean containsKeyword = page.getText().toUpperCase().contains(keyword.toUpperCase());
				if(!containsKeyword) {
					result.remove(page);
				}
			}	
		}
		
		return result.toArray(new Page[0]);
	}


	/**
	 * Load pages to memory from their directory
	 */
	public void loadPages() {

		//create the pages dir if it doesn't exist yet
		File pagesDir = new File(PAGES_DIR);
		if(!pagesDir.exists()){
			pagesDir.mkdirs();
		}

		//list and load all of the folders in there
		for(File file : pagesDir.listFiles()) {
			pagesList.add(new SinglePage(file.getPath()));
		}

		//sort the pages by time of creation
		Collections.sort(pagesList, new LastModifiedComparator());
	}



}
