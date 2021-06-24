package org.homi.plugins.scripting.vsse;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.function.Consumer;
import java.util.function.Function;

public class DirectoryWatcher  implements Runnable {

	private WatchService watcher;
	private Path dir;
	private WatchKey key;
	private Consumer<Path>  callback;
	
	public static boolean stop = false;
	
	public DirectoryWatcher(String path, Consumer<Path> callback) {
		this.dir = Paths.get(path);
		this.callback = callback;
	}
	
	@Override
	public void run() {
		try {
			watcher = FileSystems.getDefault().newWatchService();
			dir.register(watcher, 
					StandardWatchEventKinds.ENTRY_CREATE);
			while(!stop) {
				try {
					key = watcher.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				for(WatchEvent<?> event: key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();
					if(kind == StandardWatchEventKinds.OVERFLOW) {
						continue;
					}
					
					WatchEvent<Path> ev = (WatchEvent<Path>)event;
					Path filename = ev.context();
					key.reset();
					System.out.println("File added: "+ filename);
					this.callback.accept(filename);
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}