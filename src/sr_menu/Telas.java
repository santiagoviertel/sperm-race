package SR_Menu;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.game.Sprite;
import java.io.IOException;

import Teclas;
import Screen;
import ScreenInterface;
import MusicPlayer;

public class Telas extends ImagemComuns implements ScreenInterface {

	private int tecla;
	private int controleCanvas;
	private Personagem per;
	private Pista pis;
	private Pontuacao pon;
	private InicioCorrida ini;
	private PauseCorrida pac;
	private Screen s;
	private MusicPlayer musica;

	public Telas(Screen s,MusicPlayer musica) {
		this.s = s;
		this.musica = musica;
		controleCanvas = 0;
		
		int[] seq = {0, 1};
		sperm.setFrameSequence(seq);
	}

	public byte escolhaPersonagem(boolean rhino) {
		per = new Personagem();
		controleCanvas = 1;
		tecla = -2048;
		return per.launch(rhino);
	}

/*	public int escolhaPista(boolean camisinha) {
		pis = new Pista();
		controleCanvas = 2;
		tecla = -2048;
		return pis.launch(camisinha);
	}
*/
	//boolean flTipo: true para pontos e false para tempos
	public void mostraPontuacaoCampeonato(byte[] pe, int[] po, int flRecorde, boolean flTipo) {
		pon = new Pontuacao(pe, po, flTipo, flRecorde);
		controleCanvas = 3;
		tecla = -2048;
		pon.launch();
	}

	public void iniciaCorrida() {
		ini = new InicioCorrida();
		controleCanvas = 4;
		tecla = -2048;
		ini.launch();
	}
	
	public byte pausaCorrida(boolean trial){
		pac = new PauseCorrida(trial);
		controleCanvas = 5;
		tecla = -2048;
		pac.launch();
		return pac.retornaOpcao();
		
	}

	// ==================== METODOS AUXILIARES ==================== //
	private int transformaA(int alt) {
		float ALTURA = s.getHeight();	//ALTURA DA TELA DO CELULAR
		return (int)(ALTURA*((float)alt/320));
	}

	private int transformaL(int larg) {
		float LARGURA = s.getWidth();	//LARGURA DA TELA DO CELULAR
		return (int)(LARGURA*((float)larg/240));
	}

	// ================ METODOS PROPRIOS DO CANVAS ================ //
	public void paint(Graphics g) {
		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_LARGE));
		int i=0,h=g.getClipHeight()/10;
		for(int r=200; r>=150;r-=5){
		   g.setColor(r,0,0);
		   g.fillRect(0, i*h, g.getClipWidth(), h);
		   i++;
		}
		g.setColor(255,255,255);
	//	g.drawImage(veias, 0, 0, Graphics.TOP|Graphics.LEFT);
		g.drawImage(faixa, s.getWidth()/2, s.getHeight()/2+transformaA(85), Graphics.TOP|Graphics.HCENTER);
		g.drawImage(logo,(g.getClipWidth()/2),-transformaA(22), Graphics.TOP|Graphics.HCENTER);
		g.drawImage(frase, s.getWidth()/2 + transformaL(2), s.getHeight() - transformaA(17), Graphics.TOP|Graphics.HCENTER);
		sperm.defineReferencePixel(transformaL(85), 0);
		sperm.setRefPixelPosition(s.getWidth()/2, s.getHeight()-transformaA(56));
		sperm.setTransform(Sprite.TRANS_NONE);
		sperm.paint(g);
		sperm.setTransform(Sprite.TRANS_MIRROR);
		sperm.paint(g);
		sperm.defineReferencePixel(transformaL(100), 0);
		sperm.setRefPixelPosition(s.getWidth()/2, s.getHeight()-transformaA(51));
		sperm.setTransform(Sprite.TRANS_NONE);
		sperm.paint(g);
		sperm.setTransform(Sprite.TRANS_MIRROR);
		sperm.paint(g);
		switch(controleCanvas) {
			case 1:
				per.paint(g);
				break;
			case 2:
				pis.paint(g);
				break;
			case 3:
				pon.paint(g);
				break;
			case 4:
				ini.paint(g);
				break;
			case 5:
				pac.paint(g);
				break;
		}
	}

	public void keyPressed(int t) {
		tecla = t;
		System.gc();
	}

	public void keyReleased(int t) {
		tecla = -2048;
	}

	public void hideNotify() {
		musica.musicPause();
		tecla = -2048;
	}

	public void showNotify() {
		musica.musicContinue();
	}

	// ================== INICIO DAS SUB-CLASSES ================== //
	private class Personagem {

		private Image[] personagens = new Image[5];
		private Image personagem;
		private String[] nomes = new String[5];
		private byte escolhaP;
		private Image cadeado;
		private boolean flcadeado;

		public Personagem() {
			nomes[0] = "Beth";
			nomes[1] = "Tommy";
			nomes[2] = "Sammy";
			nomes[3] = "Mike";
			nomes[4] = "Rhino";
			escolhaP = 0;
			try {
				personagens[0] = Image.createImage("/SR_Menu/IM_BethChoice.png");
				personagens[1] = Image.createImage("/SR_Menu/IM_TommyChoice.png");
				personagens[2] = Image.createImage("/SR_Menu/IM_SammyChoice.png");
				personagens[3] = Image.createImage("/SR_Menu/IM_MikeChoice.png");
				personagens[4] = Image.createImage("/SR_Menu/IM_RhinoChoice.png");
				personagem = Image.createImage("/SR_Menu/IM_Character.png");
				cadeado = Image.createImage("/SR_Menu/IM_Look.png");
			}catch(IOException e) { System.out.println("Deu pau na inicializacao das Imagens do metodo 'escolhaPersonagem' da classe Telas"); }
		}

		private byte launch(boolean rhino) {
			boolean loop = true;
			while(loop) {
				if(tecla == Teclas.QUATRO || tecla == Teclas.NAVEGACAOESQUERDA) {
					escolhaP -= 1;
					if(escolhaP < 0)
						escolhaP = 4;
					    
					
					tecla = -2048;
				}else if(tecla == Teclas.SEIS || tecla == Teclas.NAVEGACAODIREITA) {
					escolhaP += 1;
					
					
					if(escolhaP > 4)
						escolhaP = 0;
					tecla = -2048;
				}
				if(escolhaP==3)
					flcadeado=false;
				else 
					flcadeado=true;
				
				if(!flcadeado && (tecla == Teclas.NAVEGACAOMEIO || tecla == Teclas.CINCO || tecla == Teclas.SOFTBUTTONESQUERDO) ) {
					loop = false;
					tecla = -2048;
					return escolhaP;
				}else if(tecla == Teclas.SOFTBUTTONDIREITO) {
					loop = false;
					tecla = -2048;
					return -125;
				}
				sperm.nextFrame();
				s.repaint();
				try {
					Thread.sleep(80);
				}catch(InterruptedException e){  }
			}
			return -120;
		}
		public void paint(Graphics g) {
			g.drawImage(back, s.getWidth() - transformaL(6),s.getHeight() - transformaA(6),Graphics.BOTTOM|Graphics.RIGHT);
			g.drawImage(ok,transformaL(6),s.getHeight() - transformaA(6),Graphics.BOTTOM|Graphics.LEFT);
			g.drawImage(setaE, s.getWidth()/2-transformaL(80), s.getHeight()/2, Graphics.TOP|Graphics.RIGHT);
			g.drawImage(setaD, s.getWidth()/2+transformaL(80), s.getHeight()/2, Graphics.TOP|Graphics.LEFT);
			g.drawImage(personagens[escolhaP], s.getWidth()/2, s.getHeight()/2, Graphics.HCENTER|Graphics.VCENTER);
			g.drawImage(personagem, s.getWidth()/2, transformaA(75), Graphics.TOP|Graphics.HCENTER);
			g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_SMALL));
			g.drawString(nomes[escolhaP], s.getWidth()/2, s.getHeight()/2+transformaA(80), Graphics.BASELINE|Graphics.HCENTER);
			if(flcadeado)
				g.drawImage(cadeado, transformaL(180), transformaA(120), Graphics.TOP|Graphics.HCENTER);
		}
	}

	private class Pista {

		private Image[] pistas = new Image[5];
		private Image pista;
		private String[] nomes = new String[5];
		private int escolhaP;
		private Image cadeado;
		private boolean flcadeado;
		/*
		public Pista() {
			nomes[0] = "Epididymis";
			nomes[1] = "Urethra";
			nomes[2] = "Condom";
			nomes[3] = "Uterus";
			nomes[4] = "Oviduct";
			escolhaP = 0;
			try {
				pistas[0] = Image.createImage("/SR_Menu/IM_TrackEpididymis.png");
				pistas[1] = Image.createImage("/SR_Menu/IM_TrackUrethra.png");
				pistas[2] = Image.createImage("/SR_Menu/IM_TrackCondom.png");
				pistas[3] = Image.createImage("/SR_Menu/IM_TrackUterus.png");
				pistas[4] = Image.createImage("/SR_Menu/IM_TrackOviduct.png");
				pista = Image.createImage("/SR_Menu/IM_Track.png");
				cadeado = Image.createImage("/SR_Menu/IM_Look.png");
			}catch(IOException e) { System.out.println("Deu pau na inicialização das Imagens do método 'escolhaPistas' da classe Telas"); }
		}

		private int launch(boolean camisinha) {
			boolean loop = true;
			while(loop) {
				if(tecla == Teclas.QUATRO || tecla == Teclas.NAVEGACAOESQUERDA) {
					escolhaP -= 1;
					if(escolhaP==2 && !camisinha)
						flcadeado = true;
					else
						flcadeado = false;
					if(escolhaP < 0)
						escolhaP = 4;
					tecla = -2048;
				}else if(tecla == Teclas.SEIS || tecla == Teclas.NAVEGACAODIREITA) {
					escolhaP += 1;
					if(escolhaP==2 && !camisinha)
						flcadeado = true;
					else
						flcadeado = false;
					if(escolhaP > 4)
						escolhaP = 0;
					tecla = -2048;
				}
				if(!flcadeado && (tecla == Teclas.NAVEGACAOMEIO || tecla == Teclas.CINCO || tecla == Teclas.SOFTBUTTONESQUERDO)) {
					loop = false;
					tecla = -2048;
					return escolhaP + 1;
				}else if(tecla == Teclas.SOFTBUTTONDIREITO) {
					loop = false;
					tecla = -2048;
					return -215;
				}
				sperm.nextFrame();
				s.repaint();
				try {
					Thread.sleep(80);
				}catch(InterruptedException e){  }
			}
			return -210;
		}
*/
		public void paint(Graphics g) {
			g.drawImage(setaE, s.getWidth()/2-transformaL(80), s.getHeight()/2, Graphics.TOP|Graphics.RIGHT);
			g.drawImage(setaD, s.getWidth()/2+transformaL(80), s.getHeight()/2, Graphics.TOP|Graphics.LEFT);
			g.drawImage(pistas[escolhaP], s.getWidth()/2, s.getHeight()/2, Graphics.VCENTER|Graphics.HCENTER);
			g.drawImage(pista, s.getWidth()/2, transformaA(80), Graphics.TOP|Graphics.HCENTER);
			g.drawImage(back, s.getWidth() - transformaL(6),s.getHeight() - transformaA(6),Graphics.BOTTOM|Graphics.RIGHT);
			g.drawImage(ok,transformaL(6),s.getHeight() - transformaA(6),Graphics.BOTTOM|Graphics.LEFT);
			g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_SMALL));
			g.drawString(nomes[escolhaP], s.getWidth()/2, s.getHeight()/2+transformaA(80), Graphics.BASELINE|Graphics.HCENTER);
			if(flcadeado)
				g.drawImage(cadeado, transformaL(180), transformaA(120), Graphics.TOP|Graphics.HCENTER);
		}
	}

	private class Pontuacao {

		private Image cabecalho;
		private String[] ranking;
		private byte[] pers;
		private int[] pont;
		private boolean flType;
		private int flRecord;
		private boolean pisca;
		private int newRecord;
//		private Image record;

		public Pontuacao(byte[] per, int[] pon, boolean flt, int flr) {
			pers = per;
			pont = pon;
			pisca = false;
			flType = flt;
			flRecord = flr;
			ranking = new String[pers.length];
			newRecord = 0;
			for(int v = 0; v < pers.length; v++) {
				if(pers[v] == 0)
					ranking[v] = "  BETH";
				else if(pers[v] == 1)
					ranking[v] = "  TOMMY";
				else if(pers[v] == 2)
					ranking[v] = "  SAMMY";
				else if(pers[v] == 3)
					ranking[v] = "  MIKE";
				else if(pers[v] == 4)
					ranking[v] = "  RHINO";
			}
//			record = null;
			try {
				if(flt)
					cabecalho = Image.createImage("/SR_Menu/IM_Championship.png");
				else
					cabecalho = Image.createImage("/SR_Menu/IM_Results.png");
	//			if(flr > -1)
	//				record = Image.createImage("/SR_Menu/IM_Record.png");
			}catch(IOException e) { System.out.println("Deu pau na inicializacao das Imagens do metodo 'mostraResultadoCampeonato' da classe Telas"); }
		}

		private void launch() {
			int count = 0;
			boolean loop = true;
			if(flRecord > -1)
				newRecord = 0;
			else
				newRecord = 1;
			while(loop) {
				switch(newRecord) {
				 	case 0:
				 		pisca = !pisca;
				 		count++;
				 		if(count == 40)
				 			newRecord = 1;
				 	break;
				 	case 1:
				 		if(tecla == Teclas.NAVEGACAOMEIO || tecla == Teclas.CINCO || tecla == Teclas.SOFTBUTTONESQUERDO) {
				 			tecla = -2048;
				 			loop = false;
				 		}
					break;
				}
				sperm.nextFrame();
				s.repaint();
				try {
					Thread.sleep(80);
				}catch(InterruptedException e){  }
			}
		}

		private String transformInt(int seg) {
			String resposta = "";
			if(seg < 60 && seg < 10) resposta =  "00:0"+String.valueOf(seg);
			if(seg < 60 && seg > 9) resposta =  "00:"+String.valueOf(seg);
			else if((seg%60) < 10) {
				resposta = "0"+String.valueOf((int)(seg/60))+":0"+String.valueOf(seg%60);
			}else if((seg%60) > 9) {
				resposta = "0"+String.valueOf((int)(seg/60))+":"+String.valueOf(seg%60);
			}
			return resposta;
		}

		public void paint(Graphics g) {
			g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
			switch(newRecord) {
				case 0:
					if(pisca) {
						g.setColor(255,216,139);
						int dx=g.getClipWidth()/2;
						int dy=g.getClipHeight()/4;
						g.setColor(252, 198, 33);
						g.drawLine(dx-34, 4+dy, dx-21, 13+dy);
						g.drawLine(dx-21, 13+dy,dx-14, 0+dy);
						g.drawLine(dx-14, 0+dy, dx-5, 12+dy);
						g.drawLine(dx-5, 12+dy,dx, 0+dy);
						g.drawLine(dx, 0+dy, dx+9, 13+dy); //Centro = x=49
						g.drawLine(dx+9, 13+dy,dx+20, 1+dy);
						g.drawLine(dx+20, 1+dy, dx+23, 17+dy);
						g.drawLine(dx+23, 17+dy,dx+38, 10+dy);
						g.drawLine(dx+38, 10+dy,dx+33, 25+dy);
						g.drawLine(dx+33, 25+dy,dx+44, 28+dy);
						g.drawLine(dx+44, 28+dy,dx+36, 37+dy);
						g.drawLine(dx+36, 37+dy,dx+44, 44+dy);
						g.drawLine(dx+44, 44+dy,dx+32, 49+dy);
						g.drawLine(dx+32, 49+dy,dx+38, 64+dy);
						g.drawLine(dx+38, 64+dy,dx+20, 59+dy);
						g.drawLine(dx+20, 59+dy,dx+14, 72+dy);
						g.drawLine(dx+14, 72+dy,dx+6, 61+dy);
						g.drawLine(dx+6, 61+dy,dx-2, 72+dy);
						g.drawLine(dx-2, 72+dy,dx-10, 62+dy);
						g.drawLine(dx-10, 62+dy,dx-18, 72+dy);
						g.drawLine(dx-18, 72+dy,dx-24, 61+dy);
						g.drawLine(dx-24, 61+dy,dx-39, 66+dy);
						g.drawLine(dx-39, 66+dy,dx-37, 54+dy);
						g.drawLine(dx-37, 54+dy,dx-47,  50+dy);
						g.drawLine(dx-47,  50+dy,dx-38, 41+dy);
						g.drawLine(dx-38, 41+dy,dx-49,  33+dy);
						g.drawLine(dx-49,  33+dy,dx-37, 28+dy);
						g.drawLine(dx-37, 28+dy,dx-40,  18+dy);
						g.drawLine(dx-40,  18+dy,dx-30, 19+dy);
						g.drawLine(dx-30, 19+dy,dx-34, 4+dy);
						g.setColor(254, 211, 133);
						g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
						g.drawString("Amazing!!",dx,17+dy, Graphics.TOP|Graphics.HCENTER);
						g.drawString("New Record",dx, 27+dy, Graphics.TOP|Graphics.HCENTER);
						g.drawString(transformInt(pont[flRecord]),dx, 43+dy, Graphics.TOP|Graphics.HCENTER);
					}
					break;
				case 1:
					g.drawImage(ok,transformaL(6),s.getHeight() - transformaA(6),Graphics.BOTTOM|Graphics.LEFT);
					if(flType) {
						g.setColor(255,200,140);
						g.drawString("1  "+ranking[0], s.getWidth()/2-transformaL(100), s.getHeight()/2-transformaA(55), Graphics.TOP|Graphics.LEFT);
						g.drawString(String.valueOf(pont[0]), s.getWidth()/2+transformaL(100), s.getHeight()/2-transformaA(55), Graphics.TOP|Graphics.RIGHT);
						g.setColor(255, 255, 255);
						g.drawString("2  "+ranking[1], s.getWidth()/2-transformaL(100), s.getHeight()/2-transformaA(20), Graphics.TOP|Graphics.LEFT);
						g.drawString(String.valueOf(pont[1]), s.getWidth()/2+transformaL(100), s.getHeight()/2-transformaA(20), Graphics.TOP|Graphics.RIGHT);
						g.setColor(15, 175, 150);
						g.drawString("3  "+ranking[2], s.getWidth()/2-transformaL(100), s.getHeight()/2+transformaA(15), Graphics.TOP|Graphics.LEFT);
						g.drawString(String.valueOf(pont[2]), s.getWidth()/2+transformaL(100), s.getHeight()/2+transformaA(15), Graphics.TOP|Graphics.RIGHT);
						g.setColor(255, 215, 0);
						g.drawString("4  "+ranking[3], s.getWidth()/2-transformaL(100), s.getHeight()/2+transformaA(50), Graphics.TOP|Graphics.LEFT);
						g.drawString(String.valueOf(pont[3]), s.getWidth()/2+transformaL(100), s.getHeight()/2+transformaA(50), Graphics.TOP|Graphics.RIGHT);
						
					}else {
						g.setColor(255, 200, 140);
						g.drawString("1  "+ranking[0], s.getWidth()/2-transformaL(100), s.getHeight()/2-transformaA(55), Graphics.TOP|Graphics.LEFT);
						g.drawString(transformInt(pont[0]), s.getWidth()/2+transformaL(100), s.getHeight()/2-transformaA(55), Graphics.TOP|Graphics.RIGHT);
						if(ranking.length>1) {
							g.setColor(255, 255, 255);
							g.drawString("2  "+ranking[1], s.getWidth()/2-transformaL(100), s.getHeight()/2-transformaA(20), Graphics.TOP|Graphics.LEFT);
							g.drawString(transformInt(pont[1]), s.getWidth()/2+transformaL(100), s.getHeight()/2-transformaA(20), Graphics.TOP|Graphics.RIGHT);
							g.setColor(15, 175, 150);
							g.drawString("3  "+ranking[2], s.getWidth()/2-transformaL(100), s.getHeight()/2+transformaA(15), Graphics.TOP|Graphics.LEFT);
							g.drawString(transformInt(pont[2]), s.getWidth()/2+transformaL(100), s.getHeight()/2+transformaA(15), Graphics.TOP|Graphics.RIGHT);
							g.setColor(255, 215, 0);
							g.drawString("4  "+ranking[3], s.getWidth()/2-transformaL(100), s.getHeight()/2+transformaA(50), Graphics.TOP|Graphics.LEFT);
							if(pont[3]!=(Integer.MAX_VALUE/1000))
								g.drawString(transformInt(pont[3]), s.getWidth()/2+transformaL(100), s.getHeight()/2+transformaA(50), Graphics.TOP|Graphics.RIGHT);
							else
								g.drawString("Out", s.getWidth()/2+transformaL(100), s.getHeight()/2+transformaA(50), Graphics.TOP|Graphics.RIGHT);
							
						}
					}
					g.drawImage(cabecalho, s.getWidth()/2, transformaA(80), Graphics.TOP|Graphics.HCENTER);
				break;
			}
		}
	}

	private class InicioCorrida {

		private boolean large;

		public InicioCorrida() {
			large = true;
		}

		public void launch() {
			boolean loop = true;
			while(loop) {
				if(large == true) {
					large = false;
				}else {
					large = true;
				}
		 		if(tecla != -2048) {
		 			loop = false;
		 		}
		 		sperm.nextFrame();
		 		s.repaint();
				try {
					Thread.sleep(100);
				}catch(InterruptedException e){  }	
			}
		}

		public void paint(Graphics g) {
			g.setColor(255, 255, 255);
			if(large) {
				g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_LARGE));
			}
			else {
				g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
			}
			g.drawString("Press Any Key", s.getWidth()/2, s.getHeight()/2-transformaA(30), Graphics.TOP|Graphics.HCENTER);
			g.drawString("To Play", s.getWidth()/2, s.getHeight()/2+transformaA(10), Graphics.TOP|Graphics.HCENTER);
		}
	}
	
	private class PauseCorrida{
		private boolean tri;
		private Image setaD;
		private byte opcao;
		private boolean escolhido;

		public PauseCorrida(boolean trial) {
			tri=trial;
			escolhido = false;
			opcao=1;
			try {
				setaD = Image.createImage("/SR_Menu/IM_RightArrow.png");
			} catch (IOException e) { }
		}
		
		public byte retornaOpcao(){
			return opcao;
		}

		public void launch() {
			
			while(!escolhido) {
				
				if(tecla == Teclas.NAVEGACAOACIMA || tecla == Teclas.DOIS) {
					if(opcao==1)
						opcao=4;
					else
						opcao--;
					
					tecla = -2048;
				}
				else if(tecla == Teclas.NAVEGACAOABAIXO || tecla == Teclas.OITO) {
					if(opcao==4)
						opcao=1;
					else
						opcao++;
					
					tecla = -2048;
				}
				else if(tecla == Teclas.NAVEGACAOMEIO || tecla == Teclas.CINCO || tecla == Teclas.SOFTBUTTONESQUERDO) {
						escolhido = true;
						s.repaint();
						try {
							Thread.sleep(300);
						}catch(InterruptedException e){  }
				}
					
		 		sperm.nextFrame();
		 		s.repaint();
				try {
					Thread.sleep(100);
				}catch(InterruptedException e){  }	
			}
		}
		
		public void paint(Graphics g) {
			g.setColor(255, 255, 255);
			g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_MEDIUM));
			
			
		
			switch(opcao){
				case 1:
					g.drawImage(setaD,s.getWidth()/2 - transformaL(65), s.getHeight()/2-transformaA(32), Graphics.TOP|Graphics.HCENTER);
					break;
				case 2:
					g.drawImage(setaD,s.getWidth()/2 - transformaL(65), s.getHeight()/2-transformaA(6), Graphics.TOP|Graphics.HCENTER);
					break;
				case 3:
					g.drawImage(setaD,s.getWidth()/2 - transformaL(65), s.getHeight()/2+transformaA(20), Graphics.TOP|Graphics.HCENTER);
					break;
				case 4:
					g.drawImage(setaD,s.getWidth()/2 - transformaL(65), s.getHeight()/2+transformaA(44), Graphics.TOP|Graphics.HCENTER);
					break;
			}
			
			if(escolhido && opcao==1){
				g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
				g.drawString("Resume", s.getWidth()/2, s.getHeight()/2-transformaA(39), Graphics.TOP|Graphics.HCENTER);
				g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_MEDIUM));
			}else
				g.drawString("Resume", s.getWidth()/2, s.getHeight()/2-transformaA(39), Graphics.TOP|Graphics.HCENTER);
			
			if(escolhido && opcao==2){
				g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
				if(tri)
				    g.drawString("Restart", s.getWidth()/2, s.getHeight()/2-transformaA(13), Graphics.TOP|Graphics.HCENTER);
				else
					g.drawString("Give Up", s.getWidth()/2, s.getHeight()/2-transformaA(13), Graphics.TOP|Graphics.HCENTER);
				g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_MEDIUM));
			}else
				if(tri)
				    g.drawString("Restart", s.getWidth()/2, s.getHeight()/2-transformaA(13), Graphics.TOP|Graphics.HCENTER);
				else
					g.drawString("Give Up", s.getWidth()/2, s.getHeight()/2-transformaA(13), Graphics.TOP|Graphics.HCENTER);
			
			if(escolhido && opcao==3){
				g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
				g.drawString("Main Menu", s.getWidth()/2, s.getHeight()/2+transformaA(13), Graphics.TOP|Graphics.HCENTER);
				g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_MEDIUM));
			}else
				g.drawString("Main Menu", s.getWidth()/2, s.getHeight()/2+transformaA(13), Graphics.TOP|Graphics.HCENTER);
			
			if(escolhido && opcao==4){
				g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
				g.drawString("Exit", s.getWidth()/2, s.getHeight()/2+transformaA(39), Graphics.TOP|Graphics.HCENTER);
				g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_MEDIUM));
			}else
				g.drawString("Exit", s.getWidth()/2, s.getHeight()/2+transformaA(39), Graphics.TOP|Graphics.HCENTER);
		}
	}
}
