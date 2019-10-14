package SR_Menu;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.io.IOException;

import Screen;
import ScreenInterface;
import MusicPlayer;


public class Animacao extends ImagemComuns implements ScreenInterface {

	private Image i1;
	private Image i2;
	private Image i3;
	//private Image i4;
	private int anima;
	private int animaW; //Variavel para case 5 - animação final (animaW e animaH)
	private int animaH;
	private Screen s;
	private Apresentacao ap;
	private MusicPlayer musica;
	private boolean pause;

	public Animacao(Screen s) {
		this.s = s;
		anima = 0;
		ap = null;
		musica = null;
		pause = false;
	}

	public void inicio() {
		anima = 3;
		try {
			i1 = Image.createImage("/SR_Menu/IM_Brand.png");
		}catch(IOException e){ System.out.println("ERRO NA CRIACAO DA IMAGEM - CASE 3 CLASSE ANIMACAO"); }
		synchronized(this) {
			try {
				s.repaint();
				wait();
			}catch(InterruptedException e) { }
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) { }
		anima = 4;
		try {
			i1 = Image.createImage("/SR_Menu/IM_GameLogo.png");
		}catch(IOException e){ System.out.println("ERRO NA CRIACAO DA IMAGEM - CASE 4 CLASSE ANIMACAO"); }
		synchronized(this) {
			try {
				s.repaint();
				wait();
			}catch(InterruptedException e) { }
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) { }
		if(pause)
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) { }
			}
	}

	public void finalJogo(int personagem,MusicPlayer musica) {
		this.musica = musica;
		synchronized(this) {
			try {
				s.repaint();
				wait();
			}catch(InterruptedException e) { }
		}
		try {
			Thread.sleep(5000);
		}catch(InterruptedException e) { }
		
		if(pause)
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) { }
			}
	}

	
	public synchronized void paint(Graphics g) {
		switch(anima) {
		case 3: 
			g.setColor(255, 255, 255);
			g.fillRect(0, 0, s.getWidth(), s.getHeight());
			g.drawImage(i1, s.getWidth()/2, s.getHeight()/2, Graphics.HCENTER|Graphics.VCENTER);
			break;
		case 4: 
			g.setColor(255, 255, 255);
			g.fillRect(0, 0, s.getWidth(), s.getHeight());
			g.drawImage(i1, s.getWidth()/2, s.getHeight()/2, Graphics.HCENTER|Graphics.VCENTER);
			break;
		case 5:
			g.setColor(255, 255, 255);
			g.fillRect(0, 0, s.getWidth(), s.getHeight());
			g.drawImage(i1, animaW, animaH, Graphics.BOTTOM|Graphics.LEFT);
			break;
		case 6:
			g.setColor(255, 255, 255);
			g.fillRect(0, 0, s.getWidth(), s.getHeight());
			g.drawImage(i2, s.getWidth()/2, s.getHeight()/2, Graphics.VCENTER|Graphics.HCENTER);
			break;
		case 7:
			g.setColor(0,0,0);
			g.fillRect(0, 0, s.getWidth(), s.getHeight());
			g.drawImage(i1, 0, 0, Graphics.TOP|Graphics.LEFT);
			g.drawImage(i2, s.getWidth() - transformaL(6), transformaA(40), Graphics.TOP|Graphics.RIGHT);
			g.drawImage(i3, s.getWidth(), s.getHeight(), Graphics.BOTTOM|Graphics.RIGHT);
			g.setColor(255,212,42);
			ap.animaConversacao(g);
			break;
		default: 	
			g.setColor(215, 0, 0);
		int i=0,h=g.getClipHeight()/10;
		for(int r=200; r>=150;r-=5){
		   g.setColor(r,0,0);
		   g.fillRect(0, i*h, g.getClipWidth(), h);
		   i++;
		}
		g.setColor(255,222,0);
		g.drawImage(logo,  s.getWidth()/2, transformaA(-22), Graphics.TOP|Graphics.HCENTER);

		//g.drawString("SPERM RACE", s.getWidth()/2, transformaA(10), Graphics.TOP|Graphics.HCENTER);
		g.drawString("Demo Version", s.getWidth()/2, transformaA(60), Graphics.TOP|Graphics.HCENTER);
		g.setColor(255,255,255);
		g.drawString("- More Tracks", s.getWidth()/6, transformaA(100), Graphics.TOP|Graphics.LEFT);
		g.drawString("- More Characters", s.getWidth()/6, transformaA(120), Graphics.TOP|Graphics.LEFT);
		g.drawString("- More Fun", s.getWidth()/6, transformaA(140), Graphics.TOP|Graphics.LEFT);
		g.setColor(255,222,0);
		g.drawString("The most important race", s.getWidth()/2, transformaA(180), Graphics.TOP|Graphics.HCENTER);
		g.drawString("of your life is coming soon", s.getWidth()/2, transformaA(200), Graphics.TOP|Graphics.HCENTER);
		g.setColor(255,255,255);
		g.drawString("www.ceugames.com", s.getWidth()/2, transformaA(230), Graphics.TOP|Graphics.HCENTER);
			break;
		}
		notify();
	}

	

	private int transformaA(int alt) {
		float ALTURA = s.getHeight();	//ALTURA DA TELA DO CELULAR
		return (int)(ALTURA*((float)alt/320));
	}

	private int transformaL(int larg) {
		float LARGURA = s.getWidth();	//LARGURA DA TELA DO CELULAR
		return (int)(LARGURA*((float)larg/240));
	}

	public void keyPressed(int t) { }

	public void keyReleased(int t) { }

	public void hideNotify() {
		pause = true;
		if(musica!=null)
			musica.musicPause();
	}

	public synchronized void showNotify() {
		pause = false;
		if(musica!=null)
			musica.musicContinue();
		notify();
	}
}