package com.luxlunaris.noadpadlight.control.classes;

import android.util.Log;

import com.luxlunaris.noadpadlight.control.interfaces.NotebookListener;
import com.luxlunaris.noadpadlight.control.interfaces.PageListener;
import com.luxlunaris.noadpadlight.control.interfaces.Pageable;
import com.luxlunaris.noadpadlight.model.classes.Compacter;
import com.luxlunaris.noadpadlight.model.classes.SinglePage;
import com.luxlunaris.noadpadlight.model.classes.comparators.LastModifiedComparator;
import com.luxlunaris.noadpadlight.model.interfaces.Page;
import com.luxlunaris.noadpadlight.model.services.FileIO;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is a facade controller that maintains a list of all of the user's pages,
 * and provides methods to create a new page, get a batch of pages of a specified size,
 * and more...
 *
 * It listens to all Pages, and it's listened to by a
 * NotebookListener.
 *
 *
 */
public class Notebook implements Pageable, PageListener {


	/**
	 * The instance of this Singleton class
	 */
	private static Notebook instance;

	/**
	 * The path to which all of the existing pages are stored
	 */
	private static final String PAGES_DIR = Paths.PAGES_DIR;

	/**
	 * The path to which deleted pages are stored (recycle bin)
	 */
	private static final String PAGES_RECYCLE_BIN = Paths.PAGES_RECYCLE_BIN;

	/**
	 * List of pages loaded in memory
	 */
	private static ArrayList<Page> pagesList;

	/**
	 * List of pages selected by the user
	 */
	private static ArrayList<Page> selectedPagesList;

	/**
	 * The list of pages in the recycle bin.
	 */
	private static ArrayList<Page> recycleBin;

	/**
	 * Current page index
	 */
	static int currentPage;

	/**
	 * Listens to this Notebook to receive updates on the status
	 * of the Pages therein.
	 */
	private static NotebookListener listener;



	private Notebook() {
		pagesList = new ArrayList<>();
		selectedPagesList = new ArrayList<>();
		recycleBin = new ArrayList<>();
		loadPages();
		loadRecycleBin();
		currentPage = 0;

	}

	/**
	 * Notebook is a Singleton
	 * @return
	 */
	public synchronized static Notebook getInstance() {
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
			addPage(page);
		}


		try {
			listener.onCreated(page);
		}catch (NullPointerException e){
			e.printStackTrace();
		}

		return page;
	}



	/**
	 * Called by a Page when it gets selected.
	 * Notebook adds it to the list of selected pages
	 * @param page
	 */
	@Override
	public void onSelected(Page page) {

		Log.d("SELECTED_PAGE", " "+page+" selected: "+page.isSelected());


		if(page.isSelected()){
			selectedPagesList.add(page);
		}else{
			selectedPagesList.remove(page);
		}
	}


	/**
	 * Returns an array of the selected pages
	 */
	public Page[] getSelected(){
		return selectedPagesList.toArray(new Page[0]);
	}



	/**
	 * When a page is deleted, it informs the Notebook
	 * @param page
	 */
	@Override
	public void onDeleted(Page page) {

		Log.d("DELETED_PAGE", page+" page is getting deleted");

		if(!page.getText().trim().isEmpty()){
			putInRecycleBin(page);
		}



		//remove the page from the "selected" list
		if(page.isSelected()){
			selectedPagesList.remove(page);
		}

		//remove the page from the pages list
		pagesList.remove(page);


		try {
			listener.onDeleted(page);
		}catch (NullPointerException e){

		}

	}

	@Override
	public void onModified(Page page) {

		try{
			listener.onModified(page);
		}catch (NullPointerException e){
		}


		//re-sort the list of pages.
		Collections.sort(pagesList, new LastModifiedComparator());

	}

	@Override
	public void onCreated(Page page) {

		//stop if page is already in the list.
		if(pagesList.contains(page)){
			return;
		}

		pagesList.add(page);
		try{
			listener.onCreated(page);
		}catch (NullPointerException e){
		}

	}


	/**
	 * Returns the next batch of pages
	 * @param amount
	 * @return
	 */
	@Override
	public Page[] getNext(int amount) {


		//calculating the amount of pages left to deliver
		amount = Math.min(amount, pagesList.size() -currentPage );

		List<Page> result = new ArrayList<>();

		try{
			result = pagesList.subList(currentPage, currentPage+amount);
			currentPage+=amount;
		}catch (Exception e){

		}

		return result.toArray(new Page[0]);

	}


	
	/**
	 * Get an array of pages by whitespace-separated keywords.
	 * @param query
	 * @return
	 */
	public void getByKeywords(String query) {

		Thread t = new Thread() {
			@Override
			public void run() {

				String[] keywords = query.split("\\s+");
				ArrayList<Page> result = new ArrayList<>(pagesList);

				for (Page page : pagesList) {
					if (page.contains(keywords)) {

						//as soon as you find a page that fits the keywords tell the
						//listener to display it.
						listener.onCreated(page);

					}
				}

			}
		};

		t.start();
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
			Page page = new SinglePage(file.getPath());
			addPage(page);
		}

		//sort the pages by time of creation
		Collections.sort(pagesList, new LastModifiedComparator());
	}


	/**
	 * Add a page to the list and start listening to it
	 * @param page
	 */
	private void addPage(Page page){
		//start listening to the new page
		page.addListener(this);
		//add the page at beginning (list sorted newest first)
		pagesList.add(0, page);
	}


	/**
	 * Get the number of loaded pages
	 * @return
	 */
	public int getPagesNum(){
		return pagesList.size();
	}


	/**
	 * Mark all Pages as selected
	 */
	public void selectAll(){
		selectedPagesList = new ArrayList<>(pagesList);
	}

	/**
	 * Mark all pages as unselected
	 */
	public void unselectAll(){
		selectedPagesList.clear();
	}


	/**
	 * Add a NotebookListener to this Notebook.
	 * @param listener
	 */
	public void setListener(NotebookListener listener){
		this.listener = listener;
	}

	/**
	 * The next batch of pages to deliver is reset to the initial one.
	 */
	public void rewind(){
		currentPage = 0;
	}

	/**
	 * Generate and return a zipped backup file that contains
	 * all of the pages' contents.
	 * @return
	 */
	public File generateBackupFile(){
		return FileIO.zipDir(PAGES_DIR, Paths.PAGES_BACKUP_DIR);
	}


	/**
	 * Import pages from a zip file.
	 * @param sourcePath
	 */
	public void importPages(String sourcePath){
		File unzipped = FileIO.unzipDir(sourcePath, sourcePath+"unzipped");

		File pagesFolder = new File(unzipped.getPath()+File.separator+"pages");

		for(File file : pagesFolder.listFiles()){

			//copy each file from the unzipped file
			try {
				FileUtils.copyDirectory(file, new File(PAGES_DIR+File.separator+file.getName()));
				Page page = new SinglePage(file.getPath());
				addPage(page);
				listener.onCreated(page);
			} catch (IOException e) {
				e.printStackTrace();
			}

			Collections.sort(pagesList, new LastModifiedComparator());
		}
	}

	/**
	 * Create a new page that has all of the contents of the selected pages,
	 * and delete the selected pages.
	 */
	public void compactSelection(){

		//create a new blank page
		Page page = newPage();

		//write the contents of the selected pages onto the blank page
		new Compacter().compact(selectedPagesList, page);

		//copy due to concurrent modification exception
		ArrayList<Page> copy = new ArrayList<>(selectedPagesList);
		//delete the old pages
		for(int i=0; i<copy.size(); i++){
			copy.get(i).delete();
		}

	}


	/**
	 * Put a page in the recycle bin:
	 * this creates a copy of the original page
	 * and places it in an alternate directory
	 *
	 * @param page
	 */
	private void putInRecycleBin(Page page){

		if(page.isInRecycleBin()){
			return;
		}

		SinglePage copy = new SinglePage(PAGES_RECYCLE_BIN+File.separator+page.getName());
		copy.create();
		ArrayList<Page> mockList = new ArrayList<>();
		mockList.add(page);
		new Compacter(false).compact(mockList, copy);
		copy.setInRecycleBin(true);

		recycleBin.add(copy);
		copy.addListener(this);

		Log.d("DELETED_PAGE", page.getName()+" copied to recycle bin as: "+copy.getPath());
		Log.d("DELETED_PAGE", "recycle bin size: "+ new File(PAGES_RECYCLE_BIN).listFiles().length);
	}

	/**
	 * Remove a page from the recycle bin and put
	 * it back with the existing pages.
	 * @param page
	 */
	private void removeFromRecycleBin(Page page){

		if(!page.isInRecycleBin()){
			return;
		}

		recycleBin.remove(page);
		Page restoredCopy = newPage(page.getName());
		ArrayList<Page> mockList = new ArrayList<>();
		mockList.add(page);
		new Compacter(false).compact(mockList, restoredCopy);
		restoredCopy.setInRecycleBin(false);
		page.delete();
		Log.d("DELETED_PAGE", page.getName()+" restored from recycle bin as: "+restoredCopy.getName());
		Log.d("DELETED_PAGE", "recycle bin size: "+ new File(PAGES_RECYCLE_BIN).listFiles().length);

	}

	/**
	 * Permanently delete all of the pages in the recycle bin.
	 * And notify the listening UI that they got deleted.
	 */
	public void emptyRecycleBin(){
		for(Page page : getRecycleBin()){
			FileIO.deleteDirectory(((File)page).getPath() );
			Log.d("DELETED_PAGE", "deleting forever: "+page.getName());
			listener.onDeleted(page);
		}
		recycleBin.clear();
		Log.d("DELETED_PAGE", "recycle bin size: "+ new File(PAGES_RECYCLE_BIN).listFiles().length);
	}

	/**
	 * Restore all pages from the recycle bin.
	 */
	public void restoreAllFromRecycleBin(){
		for(Page page : getRecycleBin()){
			removeFromRecycleBin(page);
		}
	}

	/**
	 * Get the pages in the recycle bin.
	 * @return
	 */
	public Page[] getRecycleBin(){
		return recycleBin.toArray(new Page[0]);
	}

	/**
	 * Load the pages of the recycle bin from disk.
	 */
	private void loadRecycleBin(){

		File recycleBinDir = new File(PAGES_RECYCLE_BIN);

		if(! recycleBinDir.exists()){
			new File(PAGES_RECYCLE_BIN).mkdirs();
		}

		for(File file : recycleBinDir.listFiles()){
			SinglePage page = new SinglePage(file.getPath());
			recycleBin.add(page);
			page.addListener(this);
		}
	}


	/**
	 * Restore the selected pages from the recycle bin.
	 */
	public void restoreSelection(){
		for(Page page : getSelected()){
			removeFromRecycleBin(page);
		}
		unselectAll();
	}









}
