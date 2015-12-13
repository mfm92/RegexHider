package org.softwarepraktikum.plugin.cdtregexdebugger;

import java.io.File;

import org.eclipse.cdt.core.IBinaryParser.IBinaryObject;
import org.eclipse.cdt.debug.core.ICDIDebugger2;
import org.eclipse.cdt.debug.core.cdi.ICDISession;
import org.eclipse.cdt.debug.mi.core.GDBCDIDebugger2;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;

public class CDTFoldingDebugger implements ICDIDebugger2 {
	
	GDBCDIDebugger2 debugger = new GDBCDIDebugger2();

	@Override
	public ICDISession createDebuggerSession (ILaunch launch, IBinaryObject exe, IProgressMonitor monitor)
			throws CoreException {
		System.out.println("CDTFoldingDebugger.createDebuggerSession()");
				
		ICDISession session = debugger.createDebuggerSession(launch, exe, monitor);
		
		return session;
	}

	@Override
	public ICDISession createSession (ILaunch launch, File executable, IProgressMonitor monitor)
			throws CoreException {
		System.out.println("CDTFoldingDebugger.createSession()");
		
		ICDISession session = debugger.createSession(launch, executable, monitor);

		return session;
	}
}
