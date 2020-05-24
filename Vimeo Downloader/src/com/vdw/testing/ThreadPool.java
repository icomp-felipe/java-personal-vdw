package com.vdw.testing;

public class ThreadPool {

	public static void main(String[] args) throws InterruptedException {
		
		Thread main;
		
		main = new Thread(() -> {
			
			Thread td1, td2, td3;
			
			td1 = new Thread(() -> dorme(1000));
			td2 = new Thread(() -> dorme(2000));
			td3 = new Thread(() -> dorme(3000));
			
			td1.setName("TD1");
			td2.setName("TD2");
			td3.setName("TD3");
			
			td1.start();
			td2.start();
			td3.start();
			
			try {
			
				td1.join();
				td2.join();
				td3.join();
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				System.out.println("acabou tudo caralho");
			}
			
		});
		
		main.setName("MAIN");
		main.start();
		
		System.out.println("main terminou");
		
		
	}
	
	private static void dorme(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			System.out.println(Thread.currentThread().getName() + " finished!");
		}
	}

}
