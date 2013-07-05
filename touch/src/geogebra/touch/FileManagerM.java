package geogebra.touch;

import geogebra.common.main.App;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.ListBox;

public class FileManagerM {
	private static final String FILE_PREFIX = "file#";
	private static final String THUMB_PREFIX = "thumb#";
	private static final String DATE_PREFIX = "date#";
	protected Storage stockStore = Storage.getLocalStorageIfSupported();
	public FileManagerM(){
		if(this.stockStore != null){
			ensureKeyPrefixes();
		}
	}
	
	private void ensureKeyPrefixes() {
		if (this.stockStore.getLength() > 0)
		{
			for (int i = 0; i < this.stockStore.getLength(); i++)
			{
				String oldKey = this.stockStore.key(i);
				if(!oldKey.contains("#")){
					this.stockStore.setItem(FILE_PREFIX+oldKey,this.stockStore.getItem(oldKey));
					this.stockStore.removeItem(oldKey);
				}
			}
		}	
	}
	
	public void toList(ListBox fileList){
		fileList.clear();

		if (this.stockStore == null)
		{
			return;
		}

		if (this.stockStore.getLength() > 0)
		{
			for (int i = 0; i < this.stockStore.getLength(); i++)
			{
				String key = this.stockStore.key(i);
				if(key.startsWith(FILE_PREFIX)){
					fileList.addItem(key.substring(FILE_PREFIX.length()));
				}
			}
		}

	}

	public void delete(String text) {
		this.stockStore.removeItem(FILE_PREFIX+text);
		this.stockStore.removeItem(THUMB_PREFIX+text);
		
	}

	public void saveFile(String consTitle, App app) {
		this.stockStore.setItem(FILE_PREFIX+consTitle, app.getXML());
		this.stockStore.setItem(DATE_PREFIX+consTitle,System.currentTimeMillis()+"");
		
	}

	public String getFile(String text) {
		return this.stockStore.getItem(FILE_PREFIX+text);
	}
}
