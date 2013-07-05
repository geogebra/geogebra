package geogebra.touch;

import java.util.List;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.ListBox;

public class FileManagerM {
	protected Storage stockStore = Storage.getLocalStorageIfSupported();
	
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
				fileList.addItem(this.stockStore.key(i));
			}
		}

	}

	public void delete(String text) {
		this.stockStore.removeItem(text);
		
	}

	public void saveFile(String consTitle, String xml) {
		stockStore.setItem(consTitle, xml);
		
	}

	public String getFile(String text) {
		return stockStore.getItem(text);
	}
}
