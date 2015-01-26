import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Formatter;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

public class ReminderEngine {
	
	ReminderView rv;
	Formatter saving;
	String loadingString;
	Thread timerThread;
	Thread messageThread;
	Thread soundThread;
	Clip clip;
	//arrays for storing possible String values for timerLabel
	String[] hours;
	String[] minutes;
	String[] seconds;
	//int values to store current timerLabel's time;
	int intHour;
	int intMin;
	int intSec = 0;
	
	ReminderEngine(ReminderView rv) {
		this.rv = rv;
		hours = new String[24];
		for(int i = 0; i < hours.length; i++ ) {
			hours[i] = Integer.toString(i);
		}
		minutes = new String[60];
		seconds = new String[60];
		for(int i = 0; i < 60; i++ ) {
			minutes[i] = Integer.toString(i);
			seconds[i] = Integer.toString(i);
		}
	}

	void launchTimer() {
		if (timerThread != null){
			cancelAlarm();
		}
		if (rv.getSelectedItem()!=" "){
			beginCountdown();
			printMessage();
			playSound();
		}  else {
			JOptionPane.showMessageDialog(null, 
					"Enter the number of minutes to wait");
		}
	}

	private void beginCountdown() {
		intHour = Integer.parseInt(rv.getSelectedItem()) / 60;
		intMin = Integer.parseInt(rv.getSelectedItem()) % 60;
		intSec = 0;
		updateTimerLabel();
		
		timerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(!timerThread.isInterrupted()) {
						timerThread.sleep(1000);
						if( !((intHour==0) && (intMin==0) && (intSec==0)) ) {
							setTimerLabelOneSecondLess();
						}
					}
				} catch (InterruptedException e) {
					System.out.println("Timer thread interrupted");
				}
			}
		});
		timerThread.start();
	}

	private void setTimerLabelOneSecondLess() {
		if(intSec != 0) {
			intSec--;
		} else if (intMin != 0){
			intSec = 59;
			intMin--;
		} else {
			intHour--;
			intMin = 59;
			intSec = 59;
		}
		updateTimerLabel();
	}
	
	private void updateTimerLabel() {
		rv.setTimerLabelText(hours[intHour], minutes[intMin], seconds[intSec]);
	};
	
	private void printMessage() {
		final int timeToSleep = 60000 * Integer.parseInt(rv.getSelectedItem());
		messageThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (!messageThread.isInterrupted()) {
						messageThread.sleep(timeToSleep);
						rv.setAlwaysOnTop(true);
						int flag = JOptionPane.showConfirmDialog(rv, rv.getTodoAreatext(), 
							"Wake Up!", JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE);
						rv.setAlwaysOnTop(false);
						clip.stop();
						cancelAlarm();
					}
				} catch (HeadlessException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					System.out.println("Message thread interrupted");
				}	
			}
		});	
		messageThread.start();
	}
	
	private void playSound() {
		final int timeToSleep = 60000 * Integer.parseInt(rv.getSelectedItem());
		soundThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (!soundThread.isInterrupted()) {	
						soundThread.sleep(timeToSleep);
						AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
							this.getClass().getResource("cat.wav"));
						clip = AudioSystem.getClip();
						clip.open(audioInputStream);
						while (!soundThread.isInterrupted()) {
							clip.loop(1);
						}
					}
				} catch (InterruptedException e) {
					System.out.println("Sound thread interrupted");
				} catch (UnsupportedAudioFileException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}	
			}
		});
		soundThread.start();
	}
	
	void cancelAlarm() {
		try {
			timerThread.interrupt();
			soundThread.interrupt();
			messageThread.interrupt();
			intHour = 0;
			intMin = 0;
			intSec = 0;
			updateTimerLabel();
			timerThread = null;
			soundThread = null;
			messageThread = null;
		} catch (NullPointerException e) {
			System.out.println("Null threads catched");
		}
	}
	
	void loadTodoWhenOpen() {
		try {
	    	BufferedReader br = new BufferedReader(new 
					FileReader("todo.txt"));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        loadingString = sb.toString();
	        br.close();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    rv.setTodoAreaText(loadingString);
	}

	void saveTodoWhenClose() {
		try {
			saving = new Formatter("todo.txt");
			saving.format("%s", rv.getTodoAreatext());
			saving.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}	
	}	
}
