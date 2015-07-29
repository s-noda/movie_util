package com.s_noda.movieMaker;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.net.URI;
import java.util.List;

public abstract class MovieDropAdapter extends DropTargetAdapter{
	
	public abstract void drop( File[] files ) ;
	
	@Override
	public void drop(DropTargetDropEvent e) {
		File[] files = new File[0];
		try {
			Transferable transfer = e.getTransferable();
			if (transfer
					.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				// Windows
				e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				files = (File[]) ((List<File>) (transfer
						.getTransferData(DataFlavor.javaFileListFlavor)))
						.toArray();
			} else if (transfer
					.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				// Linux
				if (e.isLocalTransfer()) {
				} else {
					e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					String str = (String) transfer
							.getTransferData(DataFlavor.stringFlavor);
					String lineSep = System
							.getProperty("line.separator");
					String[] fileList = str.split(lineSep);
					files= new File[fileList.length];
					for (int i = 0; i < files.length; i++) {
						URI fileURI = new URI(fileList[i].replaceAll("[\r\n]", ""));
						files[i] = new File(fileURI);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
//		for ( File f :files ){
//			System.out.println( f.getName() ) ;
//		}
		drop( files ) ;
	}
}
