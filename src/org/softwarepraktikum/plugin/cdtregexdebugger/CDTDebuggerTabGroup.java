package org.softwarepraktikum.plugin.cdtregexdebugger;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.launch.internal.ui.LaunchUIPlugin;
import org.eclipse.cdt.launch.ui.CLaunchConfigurationTab;
import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTabGroup;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("restriction")
public class CDTDebuggerTabGroup extends CLaunchConfigurationTab implements ILaunchConfigurationTabGroup {
	
	Text debugIDText, debugArgsText, projNameText, exeNameText, exeArgsText, platformText;
	String platform, projName, progName;
	
	private ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
		new CDTDebuggerLaunchConfigHelper()
	};

	@Override
	public void createTabs (ILaunchConfigurationDialog dialog, String mode) {
		System.out.println("CDTDebuggerTabGroup.createTabs()");
		setLaunchConfigurationDialog(dialog);
		tabs = new ILaunchConfigurationTab[] {new CDTDebuggerLaunchConfigHelper()};
	}

	@Override
	public ILaunchConfigurationTab[] getTabs () {
		System.out.println("CDTDebuggerTabGroup.getTabs()");
		return tabs;
	}

	@Override
	public void dispose () {
		System.out.println("CDTDebuggerTabGroup.dispose()");
	}

	@Override
	public void setDefaults (ILaunchConfigurationWorkingCopy config) {
		System.out.println("CDTDebuggerTabGroup.setDefaults()");
		tabs[0].setDefaults(config);
	}

	@Override
	public void initializeFrom (ILaunchConfiguration config) {
		System.out.println("CDTDebuggerTabGroup.initializeFrom()");
		
		tabs[0].initializeFrom(config);
	}

	@Override
	public void performApply (ILaunchConfigurationWorkingCopy config) {
		System.out.println("CDTDebuggerTabGroup.performApply()");

		tabs[0].performApply(config);
	}

	@Override
	public void launched (ILaunch launch) {
		System.out.println("CDTDebuggerTabGroup.launched()");
	}

	@Override
	public void createControl (Composite parent) {
		System.out.println("CDTDebuggerTabGroup.createControl()");
		
		tabs[0].createControl(parent);
	}

	@Override
	public String getName () {
		System.out.println("CDTDebuggerTabGroup.getName()");
		return "name";
	}

	protected void initializeProgramName(ICElement cElement, ILaunchConfigurationWorkingCopy config) {
		System.out.println("CDTDebuggerTabGroup.initializeProgramName()");
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
			name = getLaunchConfigurationDialog().generateName(name);
			config.rename(name);
			renamed = true;
		}

		IBinary binary = null;
		if (cElement instanceof ICProject) {
			IBinary[] bins = getBinaryFiles((ICProject)cElement);
			if (bins != null && bins.length == 1) {
				binary = bins[0];
			}
		} else if (cElement instanceof IBinary) {
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
	
	/**
	 * Iterate through and suck up all of the executable files that we can find.
	 * 
	 * This code was taken from org.eclipse.cdt.launch.ui.CMainTab.java
	 */
	protected IBinary[] getBinaryFiles(final ICProject cproject) {
		System.out.println("CDTDebuggerTabGroup.getBinaryFiles()");
		
		final Display display;
		if (cproject == null || !cproject.exists()) {
			return null;
		}
		if (getShell() == null) {
			display = LaunchUIPlugin.getShell().getDisplay();
		} else {
			display = getShell().getDisplay();
		}
		final Object[] ret = new Object[1];
		BusyIndicator.showWhile(display, () -> {
			try {
				ret[0] = cproject.getBinaryContainer().getBinaries();
			} catch (CModelException e) {
				LaunchUIPlugin.errorDialog("Launch UI internal error", e); //$NON-NLS-1$
			}
		});

		return (IBinary[])ret[0];
	}
	
}
