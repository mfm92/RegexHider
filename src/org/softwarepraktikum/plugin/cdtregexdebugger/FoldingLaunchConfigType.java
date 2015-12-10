package org.softwarepraktikum.plugin.cdtregexdebugger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

public class FoldingLaunchConfigType implements ILaunchConfigurationDelegate {
	@Override
	public void launch (ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {

		System.out.println("FoldingLaunchConfigType.launch()");
	}
}