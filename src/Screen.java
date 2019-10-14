import java.io.IOException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import SR_Menu.ImagemComuns;


public class Screen extends Canvas {

	private ScreenInterface in;
	private Loading loa;
	private Thread th;

	public Screen() {
		setFullScreenMode(true);
		loa = new Loading();
		th = new Thread(loa);
		th.start();
	}

	public void setarInterface(ScreenInterface i) {
		in = i;
	}

	public void paint(Graphics g) {
		in.paint(g);
	}

	public void keyPressed(int t){
		in.keyPressed(t);
	}

	public void keyReleased(int t){
		in.keyReleased(t);
	}

	public void hideNotify() {
		in.hideNotify();
	}

	public void showNotify() {
		in.showNotify();
	}

	public void load(){
		in = (ScreenInterface)loa;
		System.gc();
		loa.startLoading();
	}
	public void stopLoad(){
		Loading l = new Loading();
		Thread t = new Thread(l);
		t.start();
		loa.stop();
		th = t;
		loa = l;
	}

	// ==================== METODOS AUXILIARES ==================== //
	private int transformaA(int alt){
		float ALTURA = getHeight();	//ALTURA DA TELA DO CELULAR
		return (int)(ALTURA*((float)alt/320));
	}
	private int transformaL(int larg){
		float LARGURA = getWidth();	//LARGURA DA TELA DO CELULAR
		return (int)(LARGURA*((float)larg/240));
	}
	// =========================================================== //
	private class Loading extends ImagemComuns implements Runnable, ScreenInterface {

		private boolean pisca;
		private boolean looping;

		public Loading(){

			try{
				
//				veias = Image.createImage("/SR_Menu/IM_Veins.png");
				faixa = Image.createImage("/SR_Menu/IM_Banner.png");
				logo = Image.createImage("/SR_Menu/IM_Logo.png");
				frase = Image.createImage("/SR_Menu/IM_Footer.png");
				Image spriteSperm = Image.createImage("/SR_Menu/IM_AnimatedSperm.png");
				sperm = new Sprite(spriteSperm, spriteSperm.getWidth()/2,spriteSperm.getHeight());
			}catch(IOException e) { System.out.println("Deu pau na inicializacao das Imagens do construtor da subclasse Loading"); }
			int[] seq = {0, 1};
			sperm.setFrameSequence(seq);
		}

		private void stop(){
			looping = false;
		}

		private synchronized void startLoading() {
			notify();
		}

		public void run(){
			looping = true;
			pisca = true;
			synchronized(this) {
				try {
					wait();
				} catch (InterruptedException e) { }
			}
			while(looping){
				pisca=!pisca;
				sperm.nextFrame();
				repaint();
				try{
					Thread.sleep(200);
				}catch(InterruptedException e){  }
			}
		}

		public void paint(Graphics g){
			g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_LARGE));
			g.setColor(255, 255, 255);
			g.fillRect(0, 0, getWidth(), getHeight());	
			int i=0,h=g.getClipHeight()/10;
			for(int r=200; r>=150;r-=5){
			   g.setColor(r,0,0);
			   g.fillRect(0, i*h, g.getClipWidth(), h);
			   i++;
			}
			g.setColor(255,255,255);
//			g.drawImage(veias, 0, 0, Graphics.TOP|Graphics.LEFT);
			g.drawImage(faixa, getWidth()/2, getHeight()/2+transformaA(85), Graphics.TOP|Graphics.HCENTER);
			g.drawImage(logo, g.getClipWidth()/2,-transformaA(22), Graphics.TOP|Graphics.HCENTER);
			g.drawImage(frase, getWidth()/2 + transformaL(2), getHeight() - transformaA(17), Graphics.TOP|Graphics.HCENTER);
			sperm.defineReferencePixel(transformaL(85), 0);
			sperm.setRefPixelPosition(getWidth()/2, getHeight()-transformaA(56));
			sperm.setTransform(Sprite.TRANS_NONE);
			sperm.paint(g);
			sperm.setTransform(Sprite.TRANS_MIRROR);
			sperm.paint(g);
			sperm.defineReferencePixel(transformaL(100), 0);
			sperm.setRefPixelPosition(getWidth()/2, getHeight()-transformaA(51));
			sperm.setTransform(Sprite.TRANS_NONE);
			sperm.paint(g);
			sperm.setTransform(Sprite.TRANS_MIRROR);
			sperm.paint(g);
			if(pisca)
				g.drawString("Loading...", getWidth()/2, getHeight()/2, Graphics.HCENTER|Graphics.TOP);
		}

		public void keyPressed(int t) { }

		public void keyReleased(int t) { }

		public void hideNotify() { }

		public void showNotify() { }
	}
}