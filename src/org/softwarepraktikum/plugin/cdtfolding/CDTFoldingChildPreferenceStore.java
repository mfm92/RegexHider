package org.softwarepraktikum.plugin.cdtfolding;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class CDTFoldingChildPreferenceStore implements IPreferenceStore {
	
	public static final class TypeDescriptor {
		protected TypeDescriptor() { }
	}
	
	static class FoldingKey {
		TypeDescriptor type;
		String key;
		
		public FoldingKey(TypeDescriptor type, String key) {
			this.type = type;
			this.key = key;
		}
	}
	
	IPreferenceStore parent;
	IPreferenceStore child;
	IPropertyChangeListener propertyChangeListener;
	
	FoldingKey[] foldingKeys;
	
	boolean forceInit = true;
	boolean loaded;
	
	static TypeDescriptor TD_BOOLEAN = new TypeDescriptor();
	static TypeDescriptor TD_STRING = new TypeDescriptor();
	
	private class PropertyListener implements IPropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			System.out
					.println("CDTFoldingChildPreferenceStore.PropertyListener.propertyChange()");
			
			FoldingKey key = findFoldingKey(event.getProperty());
			
			if (key != null) {
				propagateProperty(key); 
			}
		}
	}
	
	public void propagate() {
		for (FoldingKey foldingKey : foldingKeys) {
			propagateProperty(foldingKey);
		}
	}
	
	private void propagateProperty (FoldingKey foldingKey) {		
//		if (parent.isDefault(foldingKey.key)) {
//			if (!child.isDefault(foldingKey.key)) {
//				child.setToDefault(foldingKey.key);
//			}
//			
//			return;
//		}
		
		System.out
				.println("CDTFoldingChildPreferenceStore.propagateProperty()");
		
		TypeDescriptor td = foldingKey.type;
		
		if (td == TD_BOOLEAN) {
			child.setValue(foldingKey.key, parent.getBoolean(foldingKey.key));
		} else if (td == TD_STRING) {
			child.setValue(foldingKey.key, parent.getString(foldingKey.key));
		}
	}
	
	public CDTFoldingChildPreferenceStore(IPreferenceStore parent, FoldingKey[] foldingMap) {
		System.out
				.println("CDTFoldingChildPreferenceStore.CDTFoldingChildPreferenceStore()");
		this.parent = parent;
		this.foldingKeys = foldingMap;
		child = new PreferenceStore();
	}
	
	private FoldingKey findFoldingKey (String key) {
		for (FoldingKey foldingKey : foldingKeys) {
			if (foldingKey.key.equals(key)) {
				return foldingKey;
			}
		}
		
		return null;
	}
	
	private boolean isInFoldingMap (String name) {
		for (FoldingKey foldKey : foldingKeys) {
			if (foldKey.key.equals(name)) {
				return true;
			}
		}
		
		return false;
	}

	public void load() {
		System.out.println("CDTFoldingChildPreferenceStore.load()");
		for (FoldingKey foldKey : foldingKeys) {
			loadProperty(foldKey);
		}
		
		loaded = true;
	}
	
	private void loadProperty (FoldingKey foldKey) {
		TypeDescriptor td = foldKey.type;
		
		if (td == TD_BOOLEAN) {
			child.setValue(foldKey.key, forceInit ? false : parent.getBoolean(foldKey.key));
			child.setDefault(foldKey.key, parent.getDefaultBoolean(foldKey.key));
		} else if (td == TD_STRING) {
			child.setValue(foldKey.key, forceInit ? "" : parent.getString(foldKey.key));
			child.setDefault(foldKey.key, parent.getDefaultString(foldKey.key));
		}
	}
	
	public void loadDefaults() {
		System.out.println("CDTFoldingChildPreferenceStore.loadDefaults()");
		
		for (FoldingKey foldingKey : foldingKeys) {
			setToDefault(foldingKey.key);
		}
	}
	
	public void start() {
		System.out.println("CDTFoldingChildPreferenceStore.start()");
		if (propertyChangeListener == null) {
			propertyChangeListener = new PropertyListener();
			parent.addPropertyChangeListener(propertyChangeListener);
		}
	}
	
	public void stop() {
		System.out.println("CDTFoldingChildPreferenceStore.stop()");
		if (propertyChangeListener == null) {
			parent.removePropertyChangeListener(propertyChangeListener);
			propertyChangeListener = null;
		}
	}
	
	@Override
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		child.addPropertyChangeListener(listener);
	}

	@Override
	public boolean contains(String name) {
		return child.contains(name);
	}

	@Override
	public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {
		child.firePropertyChangeEvent(name, oldValue, newValue);
	}

	@Override
	public boolean getBoolean(String name) {
		return child.getBoolean(name);
	}

	@Override
	public boolean getDefaultBoolean(String name) {
		return child.getDefaultBoolean(name);
	}

	@Override
	public double getDefaultDouble(String name) {
		return child.getDefaultDouble(name);
	}

	@Override
	public float getDefaultFloat(String name) {
		return child.getDefaultFloat(name);
	}

	@Override
	public int getDefaultInt(String name) {
		return child.getDefaultInt(name);
	}

	@Override
	public long getDefaultLong(String name) {
		return child.getDefaultLong(name);
	}

	@Override
	public String getDefaultString(String name) {
		return child.getDefaultString(name);
	}

	@Override
	public double getDouble(String name) {
		return child.getDefaultDouble(name);
	}

	@Override
	public float getFloat(String name) {
		return child.getFloat(name);
	}

	@Override
	public int getInt(String name) {
		return child.getInt(name);
	}

	@Override
	public long getLong(String name) {
		return child.getLong(name);
	}

	@Override
	public String getString(String name) {
		return child.getString(name);
	}

	@Override
	public boolean isDefault(String name) {
		return child.isDefault(name);
	}

	@Override
	public boolean needsSaving() {
		return child.needsSaving();
	}

	@Override
	public void putValue(String name, String value) {
		if (isInFoldingMap(name)) {
			child.putValue(name, value);
		}
	}

	@Override
	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		child.removePropertyChangeListener(listener);
	}

	@Override
	public void setDefault(String name, double value) {
		if (isInFoldingMap(name)) {
			child.setDefault(name, value);
		}
	}

	@Override
	public void setDefault(String name, float value) {
		if (isInFoldingMap(name)) {
			child.setDefault(name, value);
		}
	}

	@Override
	public void setDefault(String name, int value) {
		if (isInFoldingMap(name)) {
			child.setDefault(name, value);
		}
	}

	@Override
	public void setDefault(String name, long value) {
		if (isInFoldingMap(name)) {
			child.setDefault(name, value);
		}
	}

	@Override
	public void setDefault(String name, String defaultObject) {
		if (isInFoldingMap(name)) {
			child.setDefault(name, defaultObject);
		}
	}

	@Override
	public void setDefault(String name, boolean value) {
		if (isInFoldingMap(name)) {
			child.setDefault(name, value);
		}
	}

	@Override
	public void setToDefault(String name) {
		if (isInFoldingMap(name)) {
			child.setToDefault(name);
		}
	}

	@Override
	public void setValue(String name, double value) {
		if (isInFoldingMap(name)) {
			child.setValue(name, value);
		}
	}

	@Override
	public void setValue(String name, float value) {
		if (isInFoldingMap(name)) {
			child.setValue(name, value);
		}
	}

	@Override
	public void setValue(String name, int value) {
		if (isInFoldingMap(name)) {
			child.setValue(name, value);
		}
	}

	@Override
	public void setValue(String name, long value) {
		if (isInFoldingMap(name)) {
			child.setValue(name, value);
		}
	}

	@Override
	public void setValue(String name, String value) {
		if (isInFoldingMap(name)) {
			parent.setValue(name, value);
			child.setValue(name, value);
		}
	}

	@Override
	public void setValue(String name, boolean value) {
		if (isInFoldingMap(name)) {
			parent.setValue(name, value);
			child.setValue(name, value);
		}
	}
	
	public void addKeys(FoldingKey[] keys) {
		if (!loaded) {
			int overlayKeysLength = foldingKeys.length;
			FoldingKey[] result = new FoldingKey[keys.length + overlayKeysLength];

			for (int i = 0, length = overlayKeysLength; i < length; i++)
				result[i] = foldingKeys[i];

			for (int i = 0, length = keys.length; i < length; i++)
				result[overlayKeysLength + i] = keys[i];

			foldingKeys = result;

			load();
		}
	}
}