import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.*;


@SuppressWarnings("serial")
public class Client extends JFrame implements ActionListener{
	private TextArea primite;
	private TextArea deTrimis;
	private JButton trimite;
	private TextArea online;
	private JFrame frame;
	private MenuBar menubar;
	private Menu conversatie, about;
	private MenuItem exit, rename, sendTo, saveTxt, ab;
	String mesaj = "";
	private String name = "";
	
	Socket C;
	private DataInputStream in;
	private DataOutputStream out;
	
	public class THb extends Thread {
		public void run() {
			String aux = "";
			while(true) {
				try {
					aux = in.readUTF();
				} catch (IOException e) {
					System.out.println(e.getLocalizedMessage());
					primite.append("Conexiune inchisa");
					break;
				}
				if(!aux.equals("")) {
					char [] Tst = aux.toCharArray();
					if(Tst[0] != '@') primite.append(aux);
					else {
						String [] list = aux.split("@");
						String onl = "";
						for(int i = 1; i < list.length; i++){
							onl = onl + list[i] + "\n";
						}
						online.setText(onl);
					}
				}
			}
		}
	}
	
	public Client(){
		
		final JFrame parent  = new JFrame();
		parent.setDefaultCloseOperation(EXIT_ON_CLOSE);
		name = JOptionPane.showInputDialog(parent,"Alege nume",null);
		if(name.equals(""))  System.exit(0);
		String em = "";
		try {
			C = new Socket("localhost", 5005);
			in = new DataInputStream(C.getInputStream());
			out = new DataOutputStream(C.getOutputStream());
			out.writeUTF(name);
			em = in.readUTF();
			System.out.println(em);
			if(em.equals("Numele mai exista pe server")){
				JOptionPane.showMessageDialog(parent, ""
						+ "Numele mai exista pe server");
				System.exit(NORMAL);
			}
		} catch (IOException e) {
			
			System.out.println("Nu se conecteaza");
		}
		new THb().start();
		
		frame = new JFrame("Test");
		frame.setSize(600, 450);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel(new GridBagLayout());
		menubar = new MenuBar();
		
		frame.add(panel);
		frame.setMenuBar(menubar);
		
		GridBagConstraints c = new GridBagConstraints();
		JLabel label1 = new JLabel("Mesaje primite");
		c.gridx = 0;
		c.gridy = 0;
		panel.add(label1, c);
		
		JLabel label2 = new JLabel("Utilizatori:");
		c.gridx = 1;
		c.gridy = 0;
		panel.add(label2);
		
		primite = new TextArea(10, 45);
		c.gridx = 0;
		c.gridy = 1;
		panel.add(primite, c);
		primite.setEditable(false);
		
		online = new TextArea(10, 25);
		online.setEditable(false);
		c.gridx = 1;
		c.gridy = 1;
		online.setText("online");
		panel.add(online, c);
		
		
		deTrimis = new TextArea(10, 45);
		c.gridx = 0;
		c.gridy = 5;
		panel.add(deTrimis, c);
		
		trimite = new JButton("Trimite");
		c.gridx = 0;
		c.gridy = 6;
		panel.add(trimite, c);
		
		/*cu = new JButton("@");
		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.anchor = GridBagConstraints.WEST;
		panel.add(cu, c);
		
		change = new JButton("Schimba nume");
		c.gridx = 0;
		c.gridy = 8;
		c.anchor = GridBagConstraints.WEST;
		panel.add(change, c);
		
		
		logout = new JButton("Iesire");
		c.gridx = 0;
		c.gridy = 9;
		panel.add(logout, c);
		c.anchor = GridBagConstraints.WEST;
		*/
		
		
		
		conversatie = new Menu("Conversatie");
		menubar.add(conversatie);

		rename = new MenuItem("Schimba nume");
		conversatie.add(rename);

		sendTo = new MenuItem("Vorbeste cu");
		conversatie.add(sendTo);
		
		saveTxt = new MenuItem("Salveaza conversatia");
		conversatie.add(saveTxt);
		
		exit = new MenuItem("Delogare");
		conversatie.add(exit);
		
		about = new Menu("Despre");
		menubar.add(about);
		ab = new MenuItem("Despre program");
		about.add(ab);
		
		
		event e = new event();
		trimite.addActionListener(e);
		
		event1 ev = new event1();
		//cu.addActionListener(ev);
		sendTo.addActionListener(ev);
		
		event2 evv = new event2();
		//logout.addActionListener(evv);
		exit.addActionListener(evv);
		//change.addActionListener(this);
		
		rename.addActionListener(this);
		frame.setTitle("Chat by Kitz - Welcome " + name);
		
		event3 save= new event3();
		saveTxt.addActionListener(save);
		
		event4 desp = new event4();
		ab.addActionListener(desp);
	}
	public class event implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String sz = deTrimis.getText();
			deTrimis.setText("");
			char [] t = sz.toCharArray();
			if(t[0] == '@'){
				try {
					out.writeUTF(sz + "@" + name + "@");
				} catch (IOException ex) {
					System.out.println("Probleme la trimitere");
				}
			}
			else {
				try {
					out.writeUTF(name + ": " + sz + "\n");
				} catch (IOException ex) {
					System.out.println("Probleme la trimitere");
				}
			}
		}
		
	}
	
	public class event2 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				primite.append("\nLogged out successfully\n");
				trimite.setEnabled(false);
				in.close();
				out.close();
				C.close();
			} catch (IOException e1) {
				System.out.println("Nu s-a putut face deconectarea");
			}
			
			
		}
	}
	
	public class event1 implements ActionListener{ // pentru @
		@Override
		public void actionPerformed(ActionEvent e) {
				String cuCine = "";
				final JFrame parent  = new JFrame();
				cuCine = JOptionPane.showInputDialog(parent,"Alege nume",null);
				deTrimis.setText("@" + cuCine + "@");
		}
	}
	
	public class event3 implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			final JFrame parent  = new JFrame();
			String numeFis = JOptionPane.showInputDialog(parent,"Nume fisier",null);
			numeFis +=".txt";
			String buf = primite.getText();
			try{
				PrintWriter pw = new PrintWriter(new File (numeFis));
				pw.write(buf);
				pw.close();
				JOptionPane.showMessageDialog(parent, ""
						+ "conversatie salvata in " + numeFis + ".txt");
			}
			catch(Exception E){
				System.out.println("Probleme cu fisierul");
			}
		}
		
	}
	
	public class event4 implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			final JFrame parent  = new JFrame();
			String buff;
			buff = "Proiect 1 Programare Avansata pe Obiecte\nTimofte Bogdan - Mihai Grupa 233\nProgram de " +
							"comunicare point to point si point to all points folosind socket-uri.\nVersion 1.1";
			JOptionPane.showMessageDialog(parent, buff, "Despre program", JOptionPane.INFORMATION_MESSAGE);
			
		}
		
	}
	public static void main(String [] Args){
		new Client();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String nickNou = "";
		final JFrame parent  = new JFrame();
		nickNou = JOptionPane.showInputDialog(parent,"Alege un nou nume",null);
		try{
			out.writeUTF("~"+nickNou+"~"+name+"~");
			name = nickNou;
			this.frame.setTitle("Chat by Kitz - Welcome " + name);
		}
		catch(Exception E){
			System.out.println("Ceva a mers prost");
		}
    }
}
