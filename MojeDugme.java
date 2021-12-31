import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MojeDugme extends JButton implements ActionListener
{
	static LinkedList<Korisnik> lista=null;
	static String path;
	static File korisnici,zahtjevi;
	static Boolean isNewFile=false;
	JPanel p;
	Korisnik k;
	
	public MojeDugme(JPanel p, Korisnik k, String s)
	{
		super(s);
		addActionListener(this);
		this.p=p; this.k=k;
		if (lista==null)
			lista=new LinkedList<>();
		if (k!=null)
			lista.add(k);
	}
	
	public void actionPerformed(ActionEvent e){
		if (getText().equals("Pregled naloga")) {
			ucitajKorisnike();
			return;
		}
		
		String[] options = {"Odobrite zahtjev", "Odobrite zahtjev kao premijum", "Odbijte zahtjev"};
		int x = JOptionPane.showOptionDialog(null, "Odobrite zahtjev za registraciju",
                "Click a button",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    
        if(x==0 || x==1 || x==2) {
        	p.remove(this);
            p.revalidate();
            p.repaint();
            obrisiZahtjev();
            if (x!=2)
            	snimiKorisnika(x);
        }
  	}
	
	private void kreirajFajl()
	{
		try {
			File DSMfolder=new File(path);
			if (!DSMfolder.exists())
				DSMfolder.mkdir();
			korisnici.createNewFile();
			isNewFile=true;
		}
		catch (FileNotFoundException e) {
			System.out.println("takva datoteka ne postoji");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void snimiKorisnika(int izbor)
	{
		if (!korisnici.exists())
			kreirajFajl();
		
		if (izbor==1)
			k.premijum=true;		
		FileOutputStream fajl=null;
		ObjectOutputStream izlaz=null;
		
		try {			
			fajl = new FileOutputStream(korisnici,true);
			if(isNewFile) {
	            izlaz = new ObjectOutputStream(fajl);
	            isNewFile=false;
			}
			else {
				izlaz=new AppendableOOS(fajl);
			}
			
			izlaz.writeObject(k);
	        izlaz.close();	
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void ucitajKorisnike()
	{
		if (!korisnici.exists()) {
			JOptionPane.showMessageDialog(this, "Nema registrovanih korisnika!"); 
			return;
		}
		
		try {
			FileInputStream fin = new FileInputStream(korisnici);
	        ObjectInputStream oIn = new ObjectInputStream(fin);
	        try {
	            while (true) {
	                k = (Korisnik) oIn.readObject();
	                System.out.println(k);
	            }
	        } catch (Exception e) {
	        }
	        fin.close();
	        oIn.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Nema registrovanih korisnika!"); 
        } 
	}
	
	private void obrisiZahtjev()
	{
		//U stvari, ovaj metod brise sve korisnicke zahtjeve iz fajla i onda upisuje azuriranu listu
        lista.remove(k);
        FileOutputStream fajl=null;
		ObjectOutputStream izlaz=null;
		
		try {
			new FileOutputStream(zahtjevi).close();		//prvo brise sadrzaj cijelog fajla i odmah ga zatvara
			fajl = new FileOutputStream(zahtjevi,true);
            izlaz = new ObjectOutputStream(fajl);
            for (Korisnik temp: lista)
            	izlaz.writeObject(temp);
            izlaz.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}