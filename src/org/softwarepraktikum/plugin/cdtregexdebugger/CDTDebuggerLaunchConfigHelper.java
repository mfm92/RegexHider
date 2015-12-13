package org.softwarepraktikum.plugin.cdtregexdebugger;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.launch.ui.CLaunchConfigurationTab;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class CDTDebuggerLaunchConfigHelper extends CLaunchConfigurationTab {

	Text debugIDText, debugArgsText, projNameText, 
			exeNameText, exeArgsText, platformText;
	String platform, projName, progName;
	
	// Add text boxes to receive input parameters from the user 
	public void createControl(Composite parent) {
		System.out.println("CDTDebuggerLaunchConfigHelper.createControl()");
		// Configure the overall composite
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		gl.verticalSpacing = 20;
		gl.horizontalSpacing = 10;
		gl.marginWidth = 20;
		comp.setLayout(gl);
		
		// Create a label and text box for the debugger ID
		Label debugIDLabel = new Label(comp, SWT.NONE);
		debugIDLabel.setText("Enter the debugger ID: ");
		debugIDText = new Text(comp, SWT.BORDER);
		debugIDText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Create a label and text box for the debugger arguments
		Label debugArgsLabel = new Label(comp, SWT.NONE);
		debugArgsLabel.setText("Enter the debugger arguments: ");
		debugArgsText = new Text(comp, SWT.BORDER);
		debugArgsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Create a label and text box for the project name
		Label projNameLabel = new Label(comp, SWT.NONE);
		projNameLabel.setText("Enter the name of the project: ");
		projNameText = new Text(comp, SWT.BORDER);
		projNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Create a label and text box for the executable name
		Label exeNameLabel = new Label(comp, SWT.NONE);
		exeNameLabel.setText("Enter the name of the executable: ");
		exeNameText = new Text(comp, SWT.BORDER);
		exeNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Create a label and text box for the executable arguments
		Label exeArgsLabel = new Label(comp, SWT.NONE);
		exeArgsLabel.setText("Enter the executable arguments: ");
		exeArgsText = new Text(comp, SWT.BORDER);
		exeArgsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Create a label and text box for the executable arguments
		Label platformLabel = new Label(comp, SWT.NONE);
		platformLabel.setText("Enter the execution platform: ");
		platformText = new Text(comp, SWT.BORDER);
		platformText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		setControl(comp);
	}

	// Set a name for the configuration tag
	public String getName() {
		System.out.println("CDTDebuggerLaunchConfigHelper.getName()");
		return "Example";
	}

	// Initialize the text boxes with the configuration parameters
	public void initializeFrom(ILaunchConfiguration config) {
		System.out.println("CDTDebuggerLaunchConfigHelper.initializeFrom()");
		// You're supposed to use attribute values to initialize these boxes, but that hasn't worked out for me
		try {
			setDefaults(config.getWorkingCopy());
			debugIDText.setText("org.softwarepraktikum.plugin.cdthighlighting.ExampleDebugger");
			debugArgsText.setText(""); 
			projNameText.setText("");
			exeNameText.setText(progName); 
			exeArgsText.setText(projName); 
			platformText.setText(platform);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	// Update the attributes with values from the text boxes
	public void performApply(ILaunchConfigurationWorkingCopy config) {
		System.out.println("CDTDebuggerLaunchConfigHelper.performApply()");
		
		System.out.println("Project Name: " + projNameText.getText());
		
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_ID, debugIDText.getText());
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_SPECIFIC_ATTRS_MAP, debugArgsText.getText());
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, projNameText.getText());
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, exeNameText.getText());
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, exeArgsText.getText());
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PLATFORM, platformText.getText());
		
		try {
			System.out.println("Set to...: " + config.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""));
		} catch (Exception e) {
			// lol
		}
	}

	// Set default values for the six attributes
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		System.out.println("CDTDebuggerLaunchConfigHelper.setDefaults()");
		
		// Set the debugger name attribute
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_ID, 
				"org.softwarepraktikum.plugin.cdthighlighting.ExampleDebugger");
		
		// Set the platform attribute
		platform = Platform.getOS();
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PLATFORM, platform);
		
		// Set the rest of the attributes
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_SPECIFIC_ATTRS_MAP, "");
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "");
		
		// Set the project name and executable name attributes
		ICElement cElement = null;
		cElement = getContext(config, getPlatform(config));
		if (cElement != null) {
			initializeCProject(cElement, config);
			initializeProgramName(cElement, config);
		}
		
		try {
			projName = config.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
			progName = config.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, "");
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the program name attributes on the working copy based on the ICElement
	 * 
	 * This code was taken from org.eclipse.cdt.launch.ui.CMainTab.java
	 */
	protected void initializeProgramName(ICElement cElement, ILaunchConfigurationWorkingCopy config) {
		System.out.println("CDTDebuggerLaunchConfigHelper.initializeProgramName()");
		
		boolean renamed = false;

		if (!(cElement instanceof IBinary)) {
			cElement = cElement.getCProject();
		}
		
		if (cElement instanceof ICProject) {

			IProject project = cElement.getCProject().getProject();
			String name = project.getName();
			ICProjectDescription projDes = CCorePlugin.getDefault().getProjectDescription(project);
			if (projDes != null) {
				String buildConfigName = projDes.getActiveConfiguration().getName();
				name = name + " " + buildConfigName; //$NON-NLS-1$
			}
			config.rename(name);
			renamed = true;
		}

		IBinary binary = null;
		
		if (cElement instanceof IBinary) {
			binary = (IBinary)cElement;
		}

		if (binary != null) {
			String path;
			path = binary.getResource().getProjectRelativePath().toOSString();
			config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, path);
			if (!renamed) {
				String name = binary.getElementName();
				int index = name.lastIndexOf('.');
				if (index > 0) {
					name = name.substring(0, index);
				}
				name = getLaunchConfigurationDialog().generateName(name);
				config.rename(name);
				renamed = true;				
			}
		}
		
		if (!renamed) {
			String name = getLaunchConfigurationDialog().generateName(cElement.getCProject().getElementName());
			config.rename(name);
		}
	}
}
