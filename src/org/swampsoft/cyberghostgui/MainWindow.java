package org.swampsoft.cyberghostgui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;

public class MainWindow {
	
	JFrame mainFrame;
	JButton buttonConnect;
	JButton buttonCheck;
	
	JLabel labelOutput;
	JLabel labelLogo;
	JLabel labelPleaseWait;
	
	String stringOutput;
	
	int windowWidth;
	int windowHeight;
	
	boolean isConnected;
	
	JScrollPane scrollOutput;
	
	JComboBox<String> comboCountry;
	JComboBox<String> comboCity;
	JComboBox<String> comboStreams;
	JComboBox<String> comboServers;
	
	ArrayList<String> arrayCountries;
	ArrayList<String> arrayCountriesLong;
	ArrayList<String> arrayCities;
	ArrayList<String> arrayServers;
	ArrayList<String> arrayStreams;
	
	JPanel panelServices;
	ButtonGroup radioButtonGroupService;
	JRadioButton radioServiceTraffic;
	JRadioButton radioServiceStreaming;
	JRadioButton radioServiceTorrent;

	public MainWindow() {
		// make window
		windowWidth = 610;
		windowHeight = 550;
		
		arrayCountries = new ArrayList<String>();
		arrayCountriesLong = new ArrayList<String>();
		arrayCities = new ArrayList<String>();
		arrayServers = new ArrayList<String>();
		arrayStreams = new ArrayList<String>();
		
		mainFrame = new JFrame("Cyber Ghost");
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.setSize(windowWidth, windowHeight);
		mainFrame.setIconImage(main.imageIcon);
		mainFrame.setResizable(true);
		mainFrame.setLayout(null); // no layout, using constant positions
		mainFrame.setLocationRelativeTo(null); // centers window on screen
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (isConnected && main.isRoot) {
					System.out.println("VPN is still connnected, diconnect?");
					int result = JOptionPane.showConfirmDialog(mainFrame,"Disconnect?", "Disconnect before exiting?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

					if (result == JOptionPane.YES_OPTION) {
						System.out.println("Diconnecting...");
						disconnect();
						System.exit(0);
					}
				
					if (result == JOptionPane.NO_OPTION) {
						System.exit(0);
					}
				} else {
					System.out.println("No VPN is open or user not ROOT, exiting...");
					System.exit(0);
				}
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent arg0) {  
			    //System.out.println("deiconified " + main.tray.getTrayIcons().length);
			    mainFrame.setVisible(true);
			    if (main.tray.getTrayIcons().length > 0) {
			    	main.tray.remove(main.trayIcon);
			    }
			}  
			
			@Override
			public void windowIconified(WindowEvent arg0) {  
			    //System.out.println("iconified " + main.tray.getTrayIcons().length);
			    mainFrame.setVisible(false);
			    if (main.tray.getTrayIcons().length == 0) {
			    	try {
			    		main.tray.add(main.trayIcon);
			    	} catch (AWTException e) {
			    		//TODO Auto-generated catch block
			    		e.printStackTrace();
			    	}
			    }
			    
			}
			
			@Override
			public void windowStateChanged(WindowEvent e) {
				scrollOutput.setBounds(160,  10,  mainFrame.getWidth() - 170,  mainFrame.getHeight() - 45);
			}
		});
		mainFrame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				scrollOutput.setBounds(160,  10,  mainFrame.getWidth() - 170,  mainFrame.getHeight() - 45);
			}
		});
		
		// button CONVERT
		buttonConnect = new JButton("CONNECT");
		buttonConnect.setBackground(Color.GREEN);
		buttonConnect.setForeground(Color.WHITE);
		buttonConnect.setFont(new Font(buttonConnect.getFont().getName(), buttonConnect.getFont().getStyle(), 12));
		buttonConnect.setBounds(15,  250,  130,  40);
		buttonConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!isConnected) {
					if (main.isRoot) {
						connect();
					}
				} else {
					if (main.isRoot) {
						disconnect();
					}
				}
			}
		});
		buttonConnect.setEnabled(false);
		mainFrame.add(buttonConnect);
		
		// button CHECK
		buttonCheck = new JButton("<html><center>CHECK<br>CONNECTION</html>");
		//buttonCheck.setBackground(Color.GREEN);
		//buttonCheck.setForeground(Color.WHITE);
		buttonCheck.setFont(new Font(buttonCheck.getFont().getName(), buttonCheck.getFont().getStyle(), 12));
		buttonCheck.setBounds(15,  300,  130,  40);
		buttonCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				checkForConnection();
			}
		});
		mainFrame.add(buttonCheck);
		
		labelOutput = new JLabel(" ");
		//labelOutput.setBounds(160, 10, 200, 300);
		labelOutput.setVerticalAlignment(JLabel.TOP);
		
		scrollOutput = new JScrollPane(labelOutput);
		scrollOutput.setBounds(160,  10,  mainFrame.getWidth() - 170,  mainFrame.getHeight() - 45);
		scrollOutput.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//scrollOutput.add(labelOutput);
		mainFrame.add(scrollOutput);

		labelLogo = new JLabel(new ImageIcon(main.imageIcon));
		labelLogo.setBounds(50,  350, 64, 64);
		mainFrame.add(labelLogo);
		
		String yup = "<html><font color='red'>Uno Momento...</font></html>";
		labelPleaseWait = new JLabel(yup);
		labelPleaseWait.setBounds(27, 415, 200, 20);
		labelPleaseWait.setVisible(false);
		mainFrame.add(labelPleaseWait);
		
		radioServiceTraffic = new JRadioButton("Web Traffic", false);
		radioServiceTraffic.setSelected(false);
		radioServiceTraffic.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetCombos();
				checkCountries();
			}
		});
		
		radioServiceStreaming = new JRadioButton("Streaming", true);
		radioServiceStreaming.setSelected(false);
		radioServiceStreaming.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				resetCombos();
				checkStreams();
			}
		});
		
		radioServiceTorrent = new JRadioButton("Torrents", true);
		radioServiceTorrent.setSelected(false);
		radioServiceTorrent.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				resetCombos();
				checkCountries();
			}
		});
		
		// border for service type
		panelServices = new JPanel();
		panelServices.setBorder(BorderFactory.createTitledBorder("Service Type"));
		panelServices.setBounds(10,10, 140, 110);
		panelServices.add(radioServiceTraffic);
		panelServices.add(radioServiceStreaming);
		panelServices.add(radioServiceTorrent);
		mainFrame.add(panelServices);
		//mainFrame.getContentPane().add(panelServices);
		
		// radio group for services
		radioButtonGroupService = new ButtonGroup();
		radioButtonGroupService.add(radioServiceTraffic);
		radioButtonGroupService.add(radioServiceStreaming);
		radioButtonGroupService.add(radioServiceTorrent);
		
		comboCountry = new JComboBox<String>();
		comboCountry.addItem("Country");
		comboCountry.setBounds(15,  130, 130, 20);
		comboCountry.setEnabled(false);
		comboCountry.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (comboCountry.isEnabled() && e.getStateChange() == ItemEvent.SELECTED && !radioServiceStreaming.isSelected()) { // this is only needed for torrents and traffic
					// not streaming service
					if (!e.getItem().equals("Pick One")) {
						checkCities(arrayCountries.get(arrayCountriesLong.indexOf(e.getItem())));
					} else {
						// pick one is selected
						comboCity.setEnabled(false);
						comboCity.removeAllItems();
						comboCity.addItem("City");
						
						comboServers.setEnabled(false);
						comboServers.removeAllItems();
						comboServers.addItem("Server");
						
						buttonConnect.setEnabled(false);
					}
				} else if (comboCountry.isEnabled() && e.getStateChange() == ItemEvent.SELECTED && radioServiceStreaming.isSelected()) {
					// streaming service
					if (!e.getItem().equals("Pick One")) {
						checkCitiesStreaming(e.getItem().toString());
					} else {
						// pick one is selected
						comboCity.setEnabled(false);
						comboCity.removeAllItems();
						comboCity.addItem("City");
						
						comboServers.setEnabled(false);
						comboServers.removeAllItems();
						comboServers.addItem("Server");
						
						buttonConnect.setEnabled(false);
					}
				}
			}
		});
		mainFrame.add(comboCountry);
		
		comboCity = new JComboBox<String>();
		comboCity.addItem("City");
		comboCity.setBounds(15,  160, 130, 20);
		comboCity.setEnabled(false);
		comboCity.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (comboCity.isEnabled() && e.getStateChange() == ItemEvent.SELECTED && !radioServiceStreaming.isSelected()) {
					// not steaming
					if (!e.getItem().equals("Auto Pick")) {
						// city selected
						checkServers(e.getItem().toString());
					} else {
						// auto pick selected
						comboServers.setEnabled(false);
						comboServers.removeAllItems();
						comboServers.addItem("Server");
					}
				} else if (comboCity.isEnabled() && e.getStateChange() == ItemEvent.SELECTED && radioServiceStreaming.isSelected()) {
					// streaming
					if (!e.getItem().equals("Auto Pick")) {
						// city selected
						checkServersStreaming(e.getItem().toString());
					} else {
						// auto pick selected
						comboServers.setEnabled(false);
						comboServers.removeAllItems();
						comboServers.addItem("Server");
					}
				}
			}
		});
		mainFrame.add(comboCity);
		
		comboServers = new JComboBox<String>();
		comboServers.addItem("Server");
		comboServers.setBounds(15,  190, 130, 20);
		comboServers.setEnabled(false);
		comboServers.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (comboServers.isEnabled() && e.getStateChange() == ItemEvent.SELECTED) {
					//
				}
			}
		});
		mainFrame.add(comboServers);
		
		comboStreams = new JComboBox<String>();
		comboStreams.addItem("Stream");
		comboStreams.setBounds(15,  220, 130, 20);
		comboStreams.setEnabled(false);
		comboStreams.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (comboStreams.isEnabled() && e.getStateChange() == ItemEvent.SELECTED && arrayStreams.size() > 0) {
					if (!e.getItem().equals("Pick One")) {
						checkCountriesStreaming(arrayStreams.get(comboStreams.getSelectedIndex()));
					} else {
						// pick one is selected
					}
				}
			}
		});
		mainFrame.add(comboStreams);
		
		// Do this last after other GUI stuff
		mainFrame.setVisible(true);
		
		checkForConnection();
		//checkCountries();
		
		if (!main.isRoot) {
			appendText(" ");
			appendText("<font color= 'purple'>*** User detected: </font><font color= 'orange'>" + main.user + "</font><font color= 'purple'> ***</font>");
			appendText("<font color= 'red'>*** This app needs </font><font color= 'FUCHSIA'>root</font><font color= 'red'> access! ***</font>");
			appendText("<font color= 'red'>*** Some functions still work,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; you just can't </font><font color= 'FUCHSIA'>connect/disconnect</font><font color= 'red'>. ***</font>");
		}
		
		appendText(" ");
		appendText("<font color= 'blue'>*** Choose a service type first ***</font>");
	}
	
	private void connect() {
		labelPleaseWait.setVisible(true);
		buttonConnect.setEnabled(false);
		//main.imageIcon.getGraphics().setColor(Color.BLACK);
		new Thread() {
			public void run() {
				System.out.println("Connecting...");
		appendText("Connecting...");
		
		String service = "";
		String stream;
		String country;
		String city;
		String server;
		ArrayList<String> arrayList = new ArrayList();

		if (radioServiceTraffic.isSelected()) {
			service = "--traffic";
		} else if (radioServiceTorrent.isSelected()) {
			service = "--torrent";
		} else if (radioServiceStreaming.isSelected()) {
			service = "--streaming";
		}
		
		arrayList.add("cyberghostvpn");
		arrayList.add(service);
		if (radioServiceStreaming.isSelected()) {
			arrayList.add(arrayStreams.get(comboStreams.getSelectedIndex()));
		}
		arrayList.add("--country-code");
		
		if (!arrayCountries.get(comboCountry.getSelectedIndex()).contains(("Pick One"))) {
			country = arrayCountries.get(comboCountry.getSelectedIndex());
			arrayList.add(country);
		}
		if (!arrayCities.get(comboCity.getSelectedIndex()).contains("Auto Pick")) {
			arrayList.add("--city");
			arrayList.add(arrayCities.get(comboCity.getSelectedIndex()));
		}
		if (comboServers.isEnabled() && !arrayServers.get(comboServers.getSelectedIndex()).contains("Auto Pick")) {
			arrayList.add("--server");
			arrayList.add(arrayServers.get(comboServers.getSelectedIndex()));
		}
		
		arrayList.add("--connect");
		
		//System.out.println(arrayList);
		
		ProcessBuilder processBuilder = new ProcessBuilder(arrayList);
		Process process;
		try {
			process = processBuilder.start();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String line = "";
			while((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
				if (line.contains("VPN connection established.")) {
					isConnected = true;
					appendText("<font color= 'green'>" + line + "</font>");
				} else {
					appendText(line);
				}
			}
			bufferedReader.close();
			if (process.isAlive()) {
				process.destroy();
			}
			
			if (isConnected) {
				buttonConnect.setText("DISCONNECT");
				buttonConnect.setBackground(Color.RED);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		labelPleaseWait.setVisible(false);
		buttonConnect.setEnabled(true);
			}
		}.start();
		
	}

	private void disconnect() {
		labelPleaseWait.setVisible(true);
		
		new Thread() {
			public void run() {
				System.out.println("Disconnecting...");
				appendText("Disconnecting...");
				ProcessBuilder processBuilder = new ProcessBuilder("cyberghostvpn", "--stop");
				Process process;
				try {
					process = processBuilder.start();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					
					String line = "";
					while((line = bufferedReader.readLine()) != null) {
						System.out.println(line);
						if (line.contains("All OpenVPN processes are terminated!")) {
							appendText("<font color= 'red'>" + line + "</font>");
							isConnected = false;
						} else {
							appendText(line);
						}
					}
					bufferedReader.close();
					if (process.isAlive()) {
						process.destroy();
					}
					
					if (!isConnected) {
						resetCombos();
						radioButtonGroupService.clearSelection();
						radioServiceTraffic.setSelected(false);
						radioServiceStreaming.setSelected(false);
						radioServiceTorrent.setSelected(false);
						
						buttonConnect.setText("CONNECT");
						buttonConnect.setBackground(Color.GREEN);
						appendText(" ");
						appendText("<font color= 'blue'>*** Choose a service type first ***</font>");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				labelPleaseWait.setVisible(false);
			}
		}.start();
		
	}
	
	private void checkForConnection() {
		labelPleaseWait.setVisible(true);
		new Thread() {
			public void run() {
				System.out.println("Checking for open Connections...");
		appendText("<br>Checking for open Connections...");
		ProcessBuilder processBuilder = new ProcessBuilder("cyberghostvpn", "--status");
		Process process;
		try {
			process = processBuilder.start();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String line = "";
			while((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
				//appendText(line);
				if (line.contentEquals("No VPN connections found.")) {
					isConnected = false;
					buttonConnect.setBackground(Color.GREEN);
					buttonConnect.setText("CONNECT");
					appendText("<font color= 'red'>"+line+"</font>");
				} else if (line.contentEquals("VPN connection found.")) {
					isConnected = true;
					buttonConnect.setBackground(Color.RED);
					buttonConnect.setText("DISCONNECT");
					buttonConnect.setEnabled(true);
					appendText("<font color= 'green'>"+line+"</font>");
				}
			}
			bufferedReader.close();
			if (process.isAlive()) {
				process.destroy();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		labelPleaseWait.setVisible(false);
			}
		}.start();
		
	}
	
	private void checkCountries() {
		labelPleaseWait.setVisible(true);
		new Thread() {
			public void run() {
				String service = "";
		if (radioServiceTraffic.isSelected()) {
			service = "--traffic";
		} else if (radioServiceTorrent.isSelected()) {
			service = "--torrent";
		} else if (radioServiceStreaming.isSelected()) {
			service = "--streaming";
		}
		
		//System.out.println("Looking for countries with " + service.substring(2) + "...");
		//appendText("Looking for countries with " + service.substring(2) + "...");
		
		comboCountry.removeAllItems();
		arrayCountries.clear();
		arrayCountriesLong.clear();
		
		ProcessBuilder processBuilder = new ProcessBuilder("cyberghostvpn", service, "--country-code");
		Process process;
		try {
			process = processBuilder.start();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			comboCountry.addItem("Pick One");
			arrayCountriesLong.add("Pick One");
			arrayCountries.add("Pick One");
			
			String line = "";
			while((line = bufferedReader.readLine()) != null) {
				//System.out.println(line);
				//appendText(line);
				if (line.contains("|") && !line.contains("Country Name") && !line.contains("Country Code")){
					String temp = line.substring(line.indexOf("|", 6), line.lastIndexOf("|"));
					
					comboCountry.addItem(temp.substring(1, temp.lastIndexOf("|")).trim());
					arrayCountriesLong.add(temp.substring(1, temp.lastIndexOf("|")).trim());
					arrayCountries.add(temp.substring(temp.lastIndexOf("|")+1).trim());
					//System.out.println("Added " + arrayCountries.get(arrayCountries.size()-1) + " to countries");
					//System.out.println(temp);
				}
			}
			bufferedReader.close();
			if (process.isAlive()) {
				process.destroy();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		comboCountry.setEnabled(true);
		labelPleaseWait.setVisible(false);
		
			}
		}.start();
		
	}
	
	private void checkCountriesStreaming(String stream) {
		labelPleaseWait.setVisible(true);
		buttonConnect.setEnabled(false);
		new Thread() {
			public void run() {
				comboCountry.removeAllItems();
		arrayCountries.clear();
		arrayCountriesLong.clear();
		
		ProcessBuilder processBuilder = new ProcessBuilder("cyberghostvpn", "--streaming", stream, "--country-code");
		Process process;
		try {
			process = processBuilder.start();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			comboCountry.addItem("Pick One");
			arrayCountries.add("Pick One");
			
			String line = "";
			while((line = bufferedReader.readLine()) != null) {
				//System.out.println(line);
				//appendText(line);
				if (line.contains("|") && !line.contains("Country Name") && !line.contains("Country Code")){
					String temp = line.substring(line.indexOf("|", 6), line.lastIndexOf("|"));
					
					comboCountry.addItem(temp.substring(temp.lastIndexOf("|")+1).trim());
					//arrayCountriesLong.add(temp.substring(1, temp.lastIndexOf("|")).trim());
					arrayCountries.add(temp.substring(temp.lastIndexOf("|")+1).trim());
					//System.out.println("Added " + arrayCountries.get(arrayCountries.size()-1) + " to countries");
					//System.out.println(temp);
				}
			}
			bufferedReader.close();
			if (process.isAlive()) {
				process.destroy();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		comboCountry.setEnabled(true);
		labelPleaseWait.setVisible(false);
		buttonConnect.setEnabled(true);
		
			}
		}.start();
		
	}
	
	private void checkCities(String country) {
		labelPleaseWait.setVisible(true);
		buttonConnect.setEnabled(false);
		new Thread() {
			public void run() {
				String service = "";
		if (radioServiceTraffic.isSelected()) {
			service = "--traffic";
		} else if (radioServiceTorrent.isSelected()) {
			service = "--torrent";
		} else if (radioServiceStreaming.isSelected()) {
			service = "--streaming";
		}
		
		comboCity.removeAllItems();
		arrayCities.clear();
		
		comboCity.addItem("Auto Pick");
		arrayCities.add("Auto Pick");
		
		ProcessBuilder processBuilder = new ProcessBuilder("cyberghostvpn", service, "--country-code", country);
		Process process;
		try {
			process = processBuilder.start();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String line = "";
			while((line = bufferedReader.readLine()) != null) {
				//System.out.println(line);
				//appendText(line);
				if (line.contains("|") && !line.contains("City") && !line.contains("Instance")){
					String temp = line.substring(8, line.indexOf("|", 8)).trim();
					
					comboCity.addItem(temp);
					arrayCities.add(temp);
					//System.out.println("Added: " + arrayCities.get(arrayCities.size()-1) + " to cities array");
					//System.out.println(temp);
				}
			}
			bufferedReader.close();
			if (process.isAlive()) {
				process.destroy();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		comboCity.setEnabled(true);
		labelPleaseWait.setVisible(false);
		
		if (main.isRoot) {
			buttonConnect.setEnabled(true);
		}
			}
		}.start();
		
	}
	
	private void checkCitiesStreaming(String country) {
		labelPleaseWait.setVisible(true);
		buttonConnect.setEnabled(false);
		
		new Thread() {
			public void run() {
				comboCity.removeAllItems();
		arrayCities.clear();
		
		ProcessBuilder processBuilder = new ProcessBuilder("cyberghostvpn", "--streaming", arrayStreams.get(comboStreams.getSelectedIndex()), "--country-code", country);
		Process process;
		try {
			process = processBuilder.start();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			comboCity.addItem("Auto Pick");
			arrayCities.add("Auto Pick");
			
			String line = "";
			while((line = bufferedReader.readLine()) != null) {
				//System.out.println(line);
				//appendText(line);
				if (line.contains("|") && !line.contains("City") && !line.contains("Instance")){
					String temp = line.substring(8, line.indexOf("|", 8)).trim();
					
					comboCity.addItem(temp);
					arrayCities.add(temp);
					//System.out.println("Added: " + arrayCities.get(arrayCities.size()-1) + " to cities array");
					//System.out.println(temp);
				}
			}
			bufferedReader.close();
			if (process.isAlive()) {
				process.destroy();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (main.isRoot) {
			buttonConnect.setEnabled(true);
		}
		comboCity.setEnabled(true);
		labelPleaseWait.setVisible(false);
			}
		}.start();
		
	}
	
	private void checkServers(String city){
		labelPleaseWait.setVisible(true);
		buttonConnect.setEnabled(false);
		
		new Thread() {
			public void run() {
				String service = "";
		if (radioServiceTraffic.isSelected()) {
			service = "--traffic";
		} else if (radioServiceTorrent.isSelected()) {
			service = "--torrent";
		} else if (radioServiceStreaming.isSelected()) {
			service = "--streaming";
		}
		
		comboServers.removeAllItems();
		arrayServers.clear();
		
		comboServers.addItem("Auto Pick");
		arrayServers.add("Auto Pick");
		
		ProcessBuilder processBuilder = new ProcessBuilder("cyberghostvpn", service, "--country-code", arrayCountries.get(comboCountry.getSelectedIndex()), "--city", city);
		Process process;
		try {
			process = processBuilder.start();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String line = "";
			while((line = bufferedReader.readLine()) != null) {
				//System.out.println(line);
				//appendText(line);
				if (line.contains("|") && !line.contains("City") && !line.contains("Instance")){
					String temp = line.substring(line.indexOf("-")+1, line.indexOf("|", line.indexOf("-"))).trim();
					
					comboServers.addItem(temp + " - " + line.substring(line.indexOf("|", line.indexOf("-"))+1, line.lastIndexOf("|") - 1).trim());
					//arrayServers.add(temp);\
					arrayServers.add(line.substring(line.indexOf("|", line.indexOf("|", line.indexOf("|")+1)+1)+1, line.indexOf("|", line.indexOf("|", line.indexOf("|", line.indexOf("|")+1)+1)+1)-1).trim());
					//System.out.println("Added: " + arrayServers.get(arrayServers.size()-1) + " to servers array");
					//System.out.println(temp);
				}
			}
			bufferedReader.close();
			if (process.isAlive()) {
				process.destroy();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		comboServers.setEnabled(true);
		labelPleaseWait.setVisible(false);
		
		if (main.isRoot) {
			buttonConnect.setEnabled(true);
		}
			}
		}.start();
		
	}
	
	private void checkServersStreaming(String city){
		labelPleaseWait.setVisible(true);
		buttonConnect.setEnabled(false);
		
		new Thread() {
			public void run() {
				comboServers.removeAllItems();
		arrayServers.clear();
		
		comboServers.addItem("Auto Pick");
		arrayServers.add("Auto Pick");
		
		ProcessBuilder processBuilder = new ProcessBuilder("cyberghostvpn", "--streaming", arrayStreams.get(comboStreams.getSelectedIndex()), "--country-code", arrayCountries.get(comboCountry.getSelectedIndex()), "--city", city);
		Process process;
		try {
			process = processBuilder.start();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String line = "";
			while((line = bufferedReader.readLine()) != null) {
				//System.out.println(line);
				//appendText(line);
				if (line.contains("|") && !line.contains("City") && !line.contains("Instance")){
					String temp = line.substring(line.indexOf("-")+1, line.indexOf("|", line.indexOf("-"))).trim();
					
					comboServers.addItem(temp + " - " + line.substring(line.indexOf("|", line.indexOf("-"))+1, line.lastIndexOf("|") - 1).trim());
					//arrayServers.add(temp);
					arrayServers.add(line.substring(line.indexOf("|", line.indexOf("|", line.indexOf("|")+1)+1)+1, line.indexOf("|", line.indexOf("|", line.indexOf("|", line.indexOf("|")+1)+1)+1)-1).trim());
					//System.out.println("Added: " + arrayServers.get(arrayServers.size()-1) + " to servers array");
					//System.out.println(temp);
				}
			}
			bufferedReader.close();
			if (process.isAlive()) {
				process.destroy();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		comboServers.setEnabled(true);
		if (main.isRoot) {
			buttonConnect.setEnabled(true);
		}
		labelPleaseWait.setVisible(false);
			}
		}.start();
		
	}
	
	private void checkStreams() {
		labelPleaseWait.setVisible(true);
		new Thread() {
			public void run() {
				String service = "";
		if (radioServiceTraffic.isSelected()) {
			service = "--traffic";
		} else if (radioServiceTorrent.isSelected()) {
			service = "--torrent";
		} else if (radioServiceStreaming.isSelected()) {
			service = "--streaming";
		}
		
		//System.out.println("Looking for countries with " + service.substring(2) + "...");
		//appendText("Looking for countries with " + service.substring(2) + "...");
		
		comboStreams.removeAllItems();
		arrayStreams.clear();
		
		ProcessBuilder processBuilder = new ProcessBuilder("cyberghostvpn", service, "--country-code");
		Process process;
		try {
			process = processBuilder.start();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			comboStreams.addItem("Pick One");
			arrayStreams.add("Pick One");
			
			String line = "";
			while((line = bufferedReader.readLine()) != null) {
				//System.out.println(line);
				//appendText(line);
				if (line.contains("|") && !line.contains("Service") && !line.contains("Country Code")){
					String temp = line.substring(7, line.lastIndexOf("|"));
					
					comboStreams.addItem(temp.substring(0, temp.lastIndexOf("|")).trim());
					arrayStreams.add(temp.substring(0, temp.lastIndexOf("|")).trim());
					//arrayCountries.add(temp.substring(temp.lastIndexOf("|")+1).trim());
					//comboCountry.addItem(temp.substring(temp.lastIndexOf("|")+1).trim());
					//System.out.println("Added " + arrayCountries.get(arrayCountries.size()-1) + " to countries");
					//System.out.println(temp);
				}
			}
			bufferedReader.close();
			if (process.isAlive()) {
				process.destroy();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//comboCountry.setEnabled(true);
		comboStreams.setEnabled(true);
		labelPleaseWait.setVisible(false);
			}
			
		}.start();
	}
	
	private void appendText(String text) {
		if (stringOutput == null) {
			stringOutput = text;
		} else {
			stringOutput = stringOutput + "<br>" + text;
		}
		
		labelOutput.setText("<html>" + stringOutput + "</html>");
		new Thread(){
			public void run() {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				scrollOutput.getVerticalScrollBar().setValue(scrollOutput.getVerticalScrollBar().getMaximum());
			}
		}.start();
	}
	
	private void resetCombos() {
		buttonConnect.setEnabled(false);
		
		arrayCountriesLong.clear();
		arrayCountries.clear();
		arrayCities.clear();
		arrayServers.clear();
		arrayStreams.clear();
		
		comboCountry.setEnabled(false);
		comboCountry.removeAllItems();
		comboCountry.addItem("Country");
		
		comboCity.setEnabled(false);
		comboCity.removeAllItems();
		comboCity.addItem("City");
		
		comboStreams.setEnabled(false);
		comboStreams.removeAllItems();
		comboStreams.addItem("Stream");
		
		comboServers.setEnabled(false);
		comboServers.removeAllItems();
		comboServers.addItem("Server");
	}
}
