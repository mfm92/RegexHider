package cosy.sbg.softwarepraktikum.plugin.cdtregexdebugger;

import org.eclipse.cdt.launch.internal.LocalRunLaunchDelegate;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

/**
 * The launch configuration delegate for the CDI debugger session types.
 */
@SuppressWarnings("restriction")
public class FoldingLaunchConfigType extends LaunchConfigurationDelegate {

	LocalRunLaunchDelegate localLaunchDelegate = new LocalRunLaunchDelegate();

	public void launch (ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {

		localLaunchDelegate.launch(configuration, mode, launch, monitor);
	}

}