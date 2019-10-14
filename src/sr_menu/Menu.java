package SR_Menu;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.game.Sprite;
import java.io.IOException;

import MusicPlayer;
import Teclas;
import Screen;
import ScreenInterface;

public class Menu extends ImagemComuns implements ScreenInterface  {

	private Image newGame;
	private Image records;
	private Image instructions;
	private Image options;
	//private Image brilho;
	private Image logoEsp;
	private int controleCanvas;
	private boolean loop;
	private boolean flAnimacao;
	private int tecla;
	private MenuAbout about;
	private Animacao anima;
	private Records rec;
	private Texto escreve;
	private Setup set;
	private byte escolha;
	private Screen s;
	private MusicPlayer musica;

	// -------- INICIO DO CONSTRUTOR --------- //
	public Menu(Screen s,MusicPlayer musica) {
		this.s = s;
		try {
		//	brilho = Image.createImage("/SR_Menu/IM_Brightness.png");
			logoEsp = Image.createImage("/SR_Menu/IM_SpermLogo.png");
			setaD = Image.createImage("/SR_Menu/IM_RightArrow.png");
			setaE = Image.createImage("/SR_Menu/IM_LeftArrow.png");
			ok = Image.createImage("/SR_Menu/IM_Ok.png");
			back = Image.createImage("/SR_Menu/IM_Back.png");
			instructions = Image.createImage("/SR_Menu/IM_Instructions.png");
			options = Image.createImage("/SR_Menu/IM_Options.png");
			records = Image.createImage("/SR_Menu/IM_Records.png");
			newGame = Image.createImage("/SR_Menu/IM_NewGame.png");
		}catch(IOException e) { System.out.println("DEU PAU NA INICIALIZACAO DAS IMAGENS DO CONSTRUTOR DA CLASSE MENU"); }
		loop = true;
		tecla = -2048;
		anima = new Animacao();
		this.musica = musica;
	}

	public byte inicia() {
		byte retorno = -15;
		escolha = -1;
		while(retorno < 0) {
			flAnimacao = false;
			if(escolha != 1 && escolha != 2 && escolha != 3)
				escolha = menuSelecao();
			while(tecla != -2048) {}
			switch(escolha) {
			case 0:
				flAnimacao = true;
				retorno = menuModos();			//New Game
				//0 - INICIA TIME TRIAL
				//1 - INICIA CAMPEONATO
				//System.out.println("--- RETORNO: "+retorno);
				break;
			case 1:
				retorno = menuModos();			//Records
				rec = new Records();
				if(retorno == 0) {
					rec.mostraRTT();
					retorno = -1;	//MOSTRA RECORDES DE TIME TRIAL
				}
				else if(retorno == 1) {
					rec.mostraRC();
					retorno = -1;		//MOSTRA RECORDES DE CAMPEONATO
				}
				else
					escolha = retorno;
				break;
			case 2:
				retorno = menuInstrucoes();		//Instructions
				escreve = new Texto();
				if(retorno == 0) {
					escreve.launch(3);
					retorno = -1;	//MOSTRA INSTRUCOES DE COMANDOS (3 para instrucoes)
				}
				else if(retorno == 1) {
					escreve.launch(4);
					retorno = -1;	//MOSTRA REGRAS DO JOGO (4 para regras)
				}
				else
					escolha = retorno;
				break;
			case 3:
				retorno = menuOpcoes();			//Options
				set = new Setup();
				if (retorno == 0) {
					set.setarSom();
					retorno = -1;		//MOSTRA TELA DE SETAR SOM
				}
				else if(retorno == 1) {
					set.setarDific();
					retorno = -1;	//MOSTRA TELA DE SETAR DIFICULDADE
				}
				else
					escolha = retorno;
				break;
			case 4:
				about = new MenuAbout();		//About
				about.launch();
				break;
			case 5:
				retorno = 10;					//Exit
				break;
			default:
				break;
			}
		}
		//System.out.println("Escolha = "+escolha);
		return retorno;
	}

	// ==================  OPCOES DOS MENUS  ================== //
	private byte menuSelecao() {
		String[] menu = {"New Game", "Records", "Instructions", "Options", "About", "Exit"};
		return anima.animaMenu(menu);
	}

	private byte menuModos() {
		
		return 0;
	}

	private byte menuInstrucoes() {
		String[] instrucoes = {"Commands", "Rules"};
		return anima.animaMenu(instrucoes);
	}

	private byte menuOpcoes() {
		String[] opcoes = {"Sound", "Difficulty"};
		return anima.animaMenu(opcoes);
	}

	// ==================== METODOS AUXILIARES =================== //
	private int transformaA(int alt) {
		float ALTURA = s.getHeight();	//ALTURA DA TELA DO CELULAR
		return (int)(ALTURA*((float)alt/320));
	}

	private int transformaL(int larg) {
		float LARGURA = s.getWidth();	//LARGURA DA TELA DO CELULAR
		return (int)(LARGURA*((float)larg/240));
	}

	// =============== METODOS PROPRIOS DO CANVAS ================= //
	public void paint(Graphics g) {
		g.setFont(Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_PLAIN,Font.SIZE_MEDIUM));
		switch(controleCanvas) {
		case 1:
			rec.paint(g);
			break;
		case 2:
			rec.paint(g);
			break;
		case 3:
			escreve.paint(g);
			break;
		case 4:
			escreve.paint(g);
			break;
		case 5:
			set.paint(g);
			break;
		case 6:
			set.paint(g);
			break;
		case 7:
			about.paint(g);
			break;
		case 8:
			anima.paint(g);
			break;
		default:
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
	}

	public void showNotify() {
		musica.musicContinue();
	}

	// ======================== SUB-CLASSES ======================= //
	private class Animacao {

		
		private int[] pOvulo1, pOvulo2, pOvulo3, pOvulo4, gOvulo1, gOvulo2;
		private int pxOvulo1, pxOvulo2, pxOvulo3, pxOvulo4, pxOvulo5, gxOvulo1, gxOvulo2, gxOvulo3;
		private int estado; // 0 = parado; -1 = esquerdo; 1 = direito
		private int deltaS;
		private int deltaSG;
		private int deltaT;
		private int deltaTO;
		private int deltaAN;
		private int deltaSANx;
		private int deltaSANy;
		private int pixelX;
		private int pixelY;
		private long tempoAtual, tempoAnterior;
		private String ovulo1; 
		private String ovulo2;
		private boolean seleciona;
		private boolean showExit;
		private int a;
		private int b;
		private int xc;
		private int yc;
		private boolean secondFl;

		public Animacao() {
			estado = 0;
			deltaS = 0;
			deltaSG = 0;
			deltaSANx = 0;
			deltaSANy = 0;
			pxOvulo1 = -(int)(transformaL(35)/2);
			pxOvulo5 = s.getWidth()-pxOvulo1;
			pxOvulo3 = (int)(s.getWidth()/2);
			pxOvulo2 = (int)((pxOvulo1 + pxOvulo3)/2);
			pxOvulo4 = (int)((pxOvulo3 + pxOvulo5)/2);
			gxOvulo1 = -(int)(transformaL(145)/2);
			gxOvulo2 = (int)(s.getWidth()/2);
			gxOvulo3 = s.getWidth()-gxOvulo1;
			pOvulo1 = new int[2];
			pOvulo2 = new int[2];
			pOvulo3 = new int[2];
			pOvulo4 = new int[2];
			gOvulo1 = new int[2];
			gOvulo2 = new int[2];		
			a = (int)((s.getWidth() + transformaL(145))/2);
			b = transformaA(60);
			xc = (int)(s.getWidth()/2);
			yc = transformaA(181);
			controleCanvas = 8;
			showExit = false;
			secondFl = false;
			int[] seq = {0, 1};
			sperm.setFrameSequence(seq);
			pixelX = 120;
			pixelY = -150;
		}

		private byte animaMenu(String[] opc) {
			byte indice1 = 0;
			int indice2 = 1;
			ovulo1 = opc[indice1];
			ovulo2 = opc[indice2]; 
			loop = true;
			tempoAnterior = System.currentTimeMillis();
			controleCanvas = 8;
			seleciona = false;
			while(loop) {
				showExit = false;
				tempoAtual = System.currentTimeMillis();
				deltaT = (int)(tempoAtual-tempoAnterior);
				if(estado == 0) {
					ovulo1 = opc[indice1];
					ovulo2 = opc[indice2];
					if((tecla == Teclas.NAVEGACAOESQUERDA || tecla == Teclas.QUATRO) && secondFl == false) {
						estado = -1;
						deltaTO = 0;
						indice1 = calculaIndice(indice1, opc.length, estado);
						indice2 = calculaIndice(indice2, opc.length, estado);
						tecla = -2048;
					}
					else if((tecla == Teclas.NAVEGACAODIREITA || tecla == Teclas.SEIS) && secondFl == false) {
						estado = 1;
						deltaTO = 0;
						tecla = -2048;
					}
					else if(tecla == Teclas.CINCO || tecla == Teclas.NAVEGACAOMEIO || tecla == Teclas.SOFTBUTTONESQUERDO) {
						if(!secondFl) {
							showExit = true;
							seleciona = true;
							s.repaint();
							try {
								Thread.sleep(200);
							}catch(InterruptedException e){  }
							seleciona = false;
							deltaAN = 0;
							tecla = -2048;
							if(flAnimacao == false)
								return indice1;
							else
								secondFl = true;
						}
					}
					else if(tecla == Teclas.SOFTBUTTONDIREITO && opc[0] != "New Game") {
						tecla = -2048;
						if(opc[0] == "Time Trial" && showExit)
							return 1;
						else if(opc[0] == "Commands" && showExit)
							return 2;
						else if(opc[0] == "Sound" && showExit)
							return 3;
						else
							return -125;
					}
					calculaEixoX();
					if(opc[0] != "New Game")
						showExit = true;
				}
				else {
					deltaTO += deltaT;
					if(deltaTO >= 500) {
						if(estado == 1) {
							indice1 = calculaIndice(indice1, opc.length, estado);
							indice2 = calculaIndice(indice2, opc.length, estado);
						}
						estado = 0;
					}
					else {
						deltaS = ((deltaTO*(pxOvulo3 - pxOvulo2))/500)*estado;
						deltaSG =((deltaTO*(gxOvulo3 - gxOvulo2))/500)*estado; 
						if(estado == 1)
							moveOvulosParaDireita();
						else if(estado == -1) { //move ovulos para esquerda
							ovulo1 = opc[indice1];
							ovulo2 = opc[indice2];
							pOvulo1[0] = pxOvulo2 + deltaS;
							pOvulo2[0] = pxOvulo3 + deltaS;
							pOvulo3[0] = pxOvulo4 + deltaS;
							pOvulo4[0] = pxOvulo5 + deltaS;
							gOvulo1[0] = gxOvulo1 - deltaSG;
							gOvulo2[0] = gxOvulo2 - deltaSG;
						}
						calculaEixoY();
					}
					if(opc[0] != "New Game")
						showExit = true;
				}
				if(secondFl == true) {
					deltaAN += deltaT;
					deltaSANx = (deltaAN*90)/2000;
					deltaSANy = (deltaAN*93)/2000;
					pixelX = 120 - deltaSANx;
					pixelY = -150 + deltaSANy;
					if(deltaAN >= 2000) {
						seleciona = true;
						s.repaint();
						flAnimacao = false;
						secondFl = false;
						return indice1;
					}
				}
				s.repaint();
				tempoAnterior = tempoAtual;
				try {
					Thread.sleep(20);
				}catch(InterruptedException e){  }
			}
			return -120;	
		}

		public void paint(Graphics g) {
			g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_SMALL));
			int i=0,h=g.getClipHeight()/10;
			for(int r=200; r>=150;r-=5){
			   g.setColor(r,0,0);
			   g.fillRect(0, i*h, g.getClipWidth(), h);
			   i++;
			}
			g.setColor(255,255,255);
	//		g.drawImage(veias,0,0, Graphics.TOP|Graphics.LEFT);
			g.drawImage(logo,(g.getClipWidth()/2),-transformaA(22), Graphics.TOP|Graphics.HCENTER);
			//-- trecho de codigo que sera mostrado somente quando o usuario selecionar uma opcao
			if(seleciona == true) {
				g.setColor(255, 220, 196);
				g.fillArc(gOvulo1[0] -(int)(transformaL(155)/2), gOvulo1[1] - (int)(transformaA(145)/2), transformaL(155), transformaA(145), 0, 360);
			}
			//-----------------------------------------------------------------------------------
			//marrom
			g.setColor(148, 31, 0);
			//Ovulo grandao
			g.fillArc(gOvulo1[0] -(int)(transformaL(145)/2), gOvulo1[1] - (int)(transformaA(135)/2), transformaL(145), transformaA(135), 0, 360);
			g.fillArc(gOvulo2[0] -(int)(transformaL(145)/2), gOvulo2[1] - (int)(transformaA(135)/2), transformaL(145), transformaA(135), 0, 360);
			//Ovulos pequenos
			g.fillArc(pOvulo1[0] -(int)(transformaL(35)/2), pOvulo1[1] - (int)(transformaL(35)/2), transformaL(35), transformaA(35), 0, 360);
			g.fillArc(pOvulo2[0] -(int)(transformaL(35)/2), pOvulo2[1] - (int)(transformaL(35)/2), transformaL(35), transformaA(35), 0, 360);
			g.fillArc(pOvulo3[0] -(int)(transformaL(35)/2), pOvulo3[1] - (int)(transformaL(35)/2), transformaL(35), transformaA(35), 0, 360);
			g.fillArc(pOvulo4[0] -(int)(transformaL(35)/2), pOvulo4[1] - (int)(transformaL(35)/2), transformaL(35), transformaA(35), 0, 360);
			//laranja
			g.setColor(255, 102, 0);
			//Ovulo grande
			g.fillArc(gOvulo1[0] -(int)(transformaL(135)/2), gOvulo1[1] - (int)(transformaA(126)/2), transformaL(135), transformaA(126), 0, 360);
			g.fillArc(gOvulo2[0] -(int)(transformaL(135)/2), gOvulo2[1] - (int)(transformaA(126)/2), transformaL(135), transformaA(126), 0, 360);
			//Ovulo pequenos
			g.fillArc(pOvulo1[0] -(int)(transformaL(30)/2), pOvulo1[1] - (int)(transformaL(30)/2), transformaL(30), transformaA(30), 0, 360);
			g.fillArc(pOvulo2[0] -(int)(transformaL(30)/2), pOvulo2[1] - (int)(transformaL(30)/2), transformaL(30), transformaA(30), 0, 360);
			g.fillArc(pOvulo3[0] -(int)(transformaL(30)/2), pOvulo3[1] - (int)(transformaL(30)/2), transformaL(30), transformaA(30), 0, 360);
			g.fillArc(pOvulo4[0] -(int)(transformaL(30)/2), pOvulo4[1] - (int)(transformaL(30)/2), transformaL(30), transformaA(30), 0, 360);
			//elipses laranjas-clara
			g.setColor(255, 220, 196);
			g.fillArc(gOvulo1[0] -(int)(transformaL(135)/2), gOvulo1[1] - (int)(transformaA(35)/2), transformaL(135), transformaA(35), 0, 360);
			g.fillArc(gOvulo2[0] -(int)(transformaL(135)/2), gOvulo2[1] - (int)(transformaA(35)/2), transformaL(135), transformaA(35), 0, 360);
		//	g.drawImage(brilho,gOvulo1[0] - (int)(transformaL(117)/2) + 2, gOvulo1[1] - (int)(transformaA(115)/2), Graphics.TOP|Graphics.LEFT);
		//	g.drawImage(brilho,gOvulo2[0] - (int)(transformaL(117)/2) + 2, gOvulo2[1] - (int)(transformaA(115)/2), Graphics.TOP|Graphics.LEFT);
			g.drawImage(frase, s.getWidth()/2 + transformaL(2), s.getHeight() - transformaA(17), Graphics.TOP|Graphics.HCENTER);
			//texto
			g.setColor(0, 0, 0);
			g.drawString(ovulo1 , gOvulo1[0], gOvulo1[1]+5, Graphics.BASELINE|Graphics.HCENTER);
			g.drawString(ovulo2, gOvulo2[0], gOvulo2[1]+5, Graphics.BASELINE|Graphics.HCENTER);
			if(escolha == 0 && showExit == true)
				g.drawImage(newGame, s.getWidth()/2, transformaA(80), Graphics.TOP|Graphics.HCENTER);
			else if(escolha == 1)
				g.drawImage(records, s.getWidth()/2, transformaA(80), Graphics.TOP|Graphics.HCENTER);
			else if(escolha == 2)
				g.drawImage(instructions, s.getWidth()/2, transformaA(80), Graphics.TOP|Graphics.HCENTER);
			else if(escolha == 3)
				g.drawImage(options, s.getWidth()/2, transformaA(80), Graphics.TOP|Graphics.HCENTER);
			else
				g.drawImage(logoEsp, s.getWidth()/2, transformaA(75), Graphics.TOP|Graphics.HCENTER);
			if(estado == 0) {
				g.drawImage(setaD, s.getWidth()/2+transformaL(90), transformaA(231), Graphics.TOP|Graphics.HCENTER);
				g.drawImage(setaE, s.getWidth()/2-transformaL(90), transformaA(231), Graphics.TOP|Graphics.HCENTER);
			}
			g.drawImage(ok,transformaL(6),s.getHeight() - transformaA(6),Graphics.BOTTOM|Graphics.LEFT);
			if(secondFl == true) {
				sperm.nextFrame();
				sperm.setRefPixelPosition(s.getWidth()/2, gOvulo2[1]+transformaA(60));
				sperm.defineReferencePixel(transformaL(pixelX), transformaA(pixelY));
				sperm.setTransform(Sprite.TRANS_NONE);
				sperm.paint(g);
				sperm.setTransform(Sprite.TRANS_MIRROR);
				sperm.paint(g);
				sperm.setTransform(Sprite.TRANS_ROT180);
				sperm.paint(g);
				sperm.setTransform(Sprite.TRANS_MIRROR_ROT180);
				sperm.paint(g);
			}
			if(showExit)
				g.drawImage(back,s.getWidth() - transformaL(6),s.getHeight() - transformaA(6),Graphics.BOTTOM|Graphics.RIGHT);
		}

		// ===== METODOS AUXILIARES DA SUB-CLASSE ANIMACAO ===== //
		private byte calculaIndice(int atual, int total, int direcao) {
			byte retorno = (byte)(atual+direcao);
			atual += direcao;
			if(atual > total -1)
				retorno = 0;
			else if(atual < 0)
				retorno = (byte)(total-1);
			return retorno;
		}

		private int calculaYElipseAbaixo(int x) {
			float b1,c1,aux;
			b1 = -2*yc;
			aux = x - xc;
			c1 = yc*yc - b*b*(1 - (aux*aux)/(a*a));
			return (int)((-b1 + Math.sqrt(b1*b1 - 4*c1))/2);
		}

		private int calculaYElipseAcima(int x) {
			float b1,c1,aux;
			b1 = -2*yc;
			aux = x - xc;
			c1 = yc*yc - b*b*(1 - (aux*aux)/(a*a));
			return (int)((-b1 - Math.sqrt(b1*b1 - 4*c1))/2);
		}

		private void moveOvulosParaDireita() {
			pOvulo1[0] = pxOvulo1 + deltaS;
			pOvulo2[0] = pxOvulo2 + deltaS;
			pOvulo3[0] = pxOvulo3 + deltaS;
			pOvulo4[0] = pxOvulo4 + deltaS;
			gOvulo1[0] = gxOvulo2 - deltaSG;
			gOvulo2[0] = gxOvulo3 - deltaSG;
		}

		private void calculaEixoX() {
			pOvulo1[0] = pxOvulo2;
			pOvulo1[1] = calculaYElipseAcima(pxOvulo2);
			pOvulo2[0] = pxOvulo3;
			pOvulo2[1] = calculaYElipseAcima(pxOvulo3);
			pOvulo3[0] = pxOvulo4;
			pOvulo3[1] = calculaYElipseAcima(pxOvulo4);
			pOvulo4[0] = pxOvulo5;
			pOvulo4[1] = calculaYElipseAcima(pxOvulo5);
			gOvulo1[0] = gxOvulo2;
			gOvulo1[1] = calculaYElipseAbaixo(gxOvulo2);
			gOvulo2[0] = gxOvulo3;
			gOvulo2[1] = calculaYElipseAbaixo(gxOvulo3);
		}

		private void calculaEixoY() {
			pOvulo1[1] = calculaYElipseAcima(pOvulo1[0]);
			pOvulo2[1] = calculaYElipseAcima(pOvulo2[0]);
			pOvulo3[1] = calculaYElipseAcima(pOvulo3[0]);
			pOvulo4[1] = calculaYElipseAcima(pOvulo4[0]);
			gOvulo1[1] = calculaYElipseAbaixo(gOvulo1[0]);
			gOvulo2[1] = calculaYElipseAbaixo(gOvulo2[0]);	
		}
	}

	private class MenuAbout {


		private Image abt;
		private Sprite navDir;
		private Sprite navEsq;
		private int flTela;

		public MenuAbout() {
			try {
				abt = Image.createImage("/SR_Menu/IM_About.png");
				navDir = new Sprite(Image.createImage("/SR_Menu/IM_Navigation.png"), 7, 7);
				navEsq = new Sprite(Image.createImage("/SR_Menu/IM_Navigation.png"), 7, 7);
			}catch(IOException en) { System.out.println("DEU PAU NA INICIALIZACAO DAS IMAGENS DO CONSTRUTOR DA CLASSE MenuAbout"); }
			navDir.setFrame(1);
			navEsq.setFrame(0);
			controleCanvas = 7;
			flTela = 0;
		}

		private void launch() {
			loop = true;
			while(loop) {
				if(tecla == Teclas.SOFTBUTTONDIREITO && flTela == 1) {
					loop = false;
					tecla = -2048;
				}
				if(tecla == Teclas.NAVEGACAOESQUERDA || tecla == Teclas.QUATRO) {
					flTela = 0;
					tecla = -2048;
				}
				if(tecla == Teclas.NAVEGACAODIREITA || tecla == Teclas.SEIS) {
					flTela = 1;
					tecla = -2048;
				}
				s.repaint();
				try {
					Thread.sleep(80);
				}catch(InterruptedException e){  }
			}
		}

		public void paint(Graphics g) {
			int i=0,h=g.getClipHeight()/10;
			for(int r=200; r>=150;r-=5){
			   g.setColor(r,0,0);
			   g.fillRect(0, i*h, g.getClipWidth(), h);
			   i++;
			}
			g.setColor(255,255,255);
			g.drawImage(abt, s.getWidth()/2, transformaA(30), Graphics.TOP|Graphics.HCENTER);
			g.drawImage(frase, s.getWidth()/2 + transformaL(2), s.getHeight() - transformaA(17), Graphics.TOP|Graphics.HCENTER);
			g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_SMALL));
			if(flTela == 0) {
				g.setColor(255,222,0);
				g.drawString("Programmers", s.getWidth()/2, transformaA(60), Graphics.TOP|Graphics.HCENTER);
				g.setColor(255,255,255);
				g.drawString("Santiago Viertel", s.getWidth()/2, transformaA(86), Graphics.TOP|Graphics.HCENTER);
				g.drawString("Tatiane W. Olivette", s.getWidth()/2, transformaA(106), Graphics.TOP|Graphics.HCENTER);
				g.drawString("Thalisson C. de Almeida", s.getWidth()/2, transformaA(126), Graphics.TOP|Graphics.HCENTER);
				g.setColor(255,222,0);
				g.drawString("Music & Sounds", s.getWidth()/2, transformaA(160), Graphics.TOP|Graphics.HCENTER);
				g.setColor(255,255,255);
				g.drawString("Thiago Meister", s.getWidth()/2, transformaA(186), Graphics.TOP|Graphics.HCENTER);
				navDir.setPosition(s.getWidth()/2 + transformaL(7), s.getHeight() - transformaA(30));
				navDir.paint(g);
			}
			else if (flTela == 1) {
				g.setColor(255,222,0);
				g.drawString("Graphic Designers", s.getWidth()/2, transformaA(60), Graphics.TOP|Graphics.HCENTER);
				g.setColor(255,255,255);
				g.drawString("David A. F. Neto", s.getWidth()/2, transformaA(86), Graphics.TOP|Graphics.HCENTER);
				g.drawString("Filipe Leal", s.getWidth()/2, transformaA(106), Graphics.TOP|Graphics.HCENTER);
				g.setColor(255,222,0);
				g.drawString("Game Designer", s.getWidth()/2, transformaA(160), Graphics.TOP|Graphics.HCENTER);
				g.setColor(255,255,255);
				g.drawString("Fabiano N. de Oliveira", s.getWidth()/2, transformaA(186), Graphics.TOP|Graphics.HCENTER);
				g.drawImage(back, s.getWidth() - transformaL(6),s.getHeight() - transformaA(6),Graphics.BOTTOM|Graphics.RIGHT);
				navEsq.setPosition(s.getWidth()/2  - transformaL(7), s.getHeight() - transformaA(30));
				navEsq.paint(g);
			}
			
		}
	}

	private class Records {

		private short[] temposRTT;
		private short[] temposRC;

		public Records() {
			try {
				records = Image.createImage("/SR_Menu/IM_Records.png");	//usado em: mostraRTT e mostraRC
			}catch(IOException e) { System.out.println("DEU PAU NA INICIALIZACAO DAS IMAGENS DO CONSTRUTOR DA CLASSE Records"); }
			int[] seq = {0, 1};
			sperm.setFrameSequence(seq);
			sperm.setRefPixelPosition(-70, 0);
		}

		private void mostraRTT() {
			controleCanvas = 1;
			BD bancoRTT = new BD("timetrial",0);
			temposRTT = bancoRTT.leRecordes();
			bancoRTT.fechar();
			loop = true;
			while(loop) {
				if(tecla == Teclas.SOFTBUTTONDIREITO) {
					loop = false;
					tecla = -2048;
				}
				sperm.nextFrame();
				s.repaint();
				try {
					Thread.sleep(100);
				}catch(InterruptedException e){  }
			}
		}

		private void mostraRC() {
			controleCanvas = 2;
			BD bancoRC = new BD("championship",0);
			temposRC = bancoRC.leRecordes();
			bancoRC.fechar();
			loop = true;
			while(loop) {
				if(tecla == Teclas.SOFTBUTTONDIREITO) {
					loop = false;
					tecla = -2048;
				}
				sperm.nextFrame();
				s.repaint();
				try{
					Thread.sleep(100);
				}catch(InterruptedException e){  }
			}
		}

		private String setRecord(short seg) {
			String resposta = "";
			if(seg < 60 && seg < 10) resposta =  "00:0"+String.valueOf(seg);
			if(seg < 60 && seg > 9) resposta =  "00:"+String.valueOf(seg);
			else if((seg%60) < 10)
				resposta = "0"+String.valueOf((int)(seg/60))+":0"+String.valueOf(seg%60);
			else if((seg%60) > 9)
				resposta = "0"+String.valueOf((int)(seg/60))+":"+String.valueOf(seg%60);
			return resposta;
		}

		public void paint(Graphics g) {
			int i=0,h=g.getClipHeight()/10;
			for(int r=200; r>=150;r-=5){
			   g.setColor(r,0,0);
			   g.fillRect(0, i*h, g.getClipWidth(), h);
			   i++;
			}
			g.setColor(255,255,255);
		//	g.drawImage(veias, 0, 0, Graphics.TOP|Graphics.LEFT);
			g.drawImage(back, s.getWidth() - transformaL(6),s.getHeight() - transformaA(6),Graphics.BOTTOM|Graphics.RIGHT);
			g.drawImage(faixa, s.getWidth()/2, s.getHeight()-transformaA(65), Graphics.TOP|Graphics.HCENTER);
			g.drawImage(logo,(g.getClipWidth()/2),-transformaA(22), Graphics.TOP|Graphics.HCENTER);
			g.drawImage(records, s.getWidth()/2, transformaA(75), Graphics.TOP|Graphics.HCENTER);
			g.drawImage(frase, s.getWidth()/2 + transformaL(2), s.getHeight() - transformaA(17), Graphics.TOP|Graphics.HCENTER);
			g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_MEDIUM));
			g.drawString("Epididymis", s.getWidth()/2 - transformaL(90), s.getHeight()/2 - transformaA(16), Graphics.BASELINE|Graphics.LEFT);
			g.drawString("Urethra", s.getWidth()/2 - transformaL(90), s.getHeight()/2 + transformaA(6), Graphics.BASELINE|Graphics.LEFT);
			g.drawString("Condom", s.getWidth()/2 - transformaL(90), s.getHeight()/2 + transformaA(28), Graphics.BASELINE|Graphics.LEFT);
			g.drawString("Uterus", s.getWidth()/2 - transformaL(90), s.getHeight()/2 + transformaA(50), Graphics.BASELINE|Graphics.LEFT);
			g.drawString("Oviduct", s.getWidth()/2 - transformaL(90), s.getHeight()/2 + transformaA(72), Graphics.BASELINE|Graphics.LEFT);
			if(controleCanvas == 1) {
				g.drawString("Time Trial", s.getWidth()/2, transformaA(95), Graphics.TOP|Graphics.HCENTER);
				g.drawString(setRecord(temposRTT[0]), s.getWidth()/2+transformaL(90), s.getHeight()/2 - transformaA(16), Graphics.BASELINE|Graphics.RIGHT);
				g.drawString(setRecord(temposRTT[1]), s.getWidth()/2+transformaL(90), s.getHeight()/2 + transformaA(6), Graphics.BASELINE|Graphics.RIGHT);
				g.drawString(setRecord(temposRTT[2]), s.getWidth()/2+transformaL(90), s.getHeight()/2 + transformaA(28), Graphics.BASELINE|Graphics.RIGHT);
				g.drawString(setRecord(temposRTT[3]), s.getWidth()/2+transformaL(90), s.getHeight()/2 + transformaA(50), Graphics.BASELINE|Graphics.RIGHT);
				g.drawString(setRecord(temposRTT[4]), s.getWidth()/2+transformaL(90), s.getHeight()/2 + transformaA(72), Graphics.BASELINE|Graphics.RIGHT);
			}
			else if(controleCanvas == 2) {
				g.drawString("Championship", s.getWidth()/2, transformaA(95), Graphics.TOP|Graphics.HCENTER);
				g.drawString(setRecord(temposRC[0]), s.getWidth()/2+transformaL(90), s.getHeight()/2 - transformaA(16), Graphics.BASELINE|Graphics.RIGHT);
				g.drawString(setRecord(temposRC[1]), s.getWidth()/2+transformaL(90), s.getHeight()/2 + transformaA(6), Graphics.BASELINE|Graphics.RIGHT);
				g.drawString(setRecord(temposRC[2]), s.getWidth()/2+transformaL(90), s.getHeight()/2 + transformaA(28), Graphics.BASELINE|Graphics.RIGHT);
				g.drawString(setRecord(temposRC[3]), s.getWidth()/2+transformaL(90), s.getHeight()/2 + transformaA(50), Graphics.BASELINE|Graphics.RIGHT);
				g.drawString(setRecord(temposRC[4]), s.getWidth()/2+transformaL(90), s.getHeight()/2 + transformaA(72), Graphics.BASELINE|Graphics.RIGHT);
			}
		}
	}

	private class Texto {

		private Apresentacao texto;

		public Texto() {
			texto = new Apresentacao(s.getWidth()/2,transformaA(10),s.getWidth() - transformaL(28),s.getHeight()-transformaA(20));
		}

		private void launch(int opt) {
			if(opt == 3) {
				controleCanvas = 3;
				texto.setarString("Commands: Use the keyboard keys 4 to move left and 6 to move right.");
			}
			else if(opt == 4) {
				controleCanvas = 4;
				texto.setarString("Rules: Help the spermatozoon to arrive at ovum in the most important race of your life. Stay in first place in the total of score in the end of the championship, winning the competition and entering in ovum.");
			}
			loop = true;
			while(loop) {
				if(tecla == Teclas.QUATRO || tecla == Teclas.NAVEGACAOESQUERDA) {		//tecla 4
					texto.textoAnterior();
					tecla = -2048;
				}else if(tecla == Teclas.SEIS || tecla == Teclas.NAVEGACAODIREITA) {	//tecla 6
					texto.proximoTexto();
					tecla = -2048;
				}
				if(tecla == Teclas.SOFTBUTTONDIREITO) {
					loop = false;
					tecla = -2048;
				}
				s.repaint();
				try {
					Thread.sleep(20);
				}catch(InterruptedException e){  }
			}
		}

		public void paint(Graphics g) {
			int i=0,h=g.getClipHeight()/10;
			for(int r=200; r>=150;r-=5){
			   g.setColor(r,0,0);
			   g.fillRect(0, i*h, g.getClipWidth(), h);
			   i++;
			}
			g.setColor(255,255,255);
	//		g.drawImage(veias, 0, 0, Graphics.TOP|Graphics.LEFT);
			g.drawImage(back, s.getWidth() - transformaL(6),s.getHeight() - transformaA(6), Graphics.BOTTOM|Graphics.RIGHT);
			g.drawImage(frase, s.getWidth()/2 + transformaL(2), s.getHeight() - transformaA(17), Graphics.TOP|Graphics.HCENTER);
			g.setColor(255,255,255);
			texto.animaConversacao(g);
		}
	}

	private class Setup {

		private int[] ySeta = {-50, -28, -6, +16}; //usado para setar o som
		private int[] xSeta = {-30, -8, +14}; //usado para setar o nivel de dificuldade
		private short opcaoEscolhidaSom;
		private short opcaoEscolhidaDific;
		private int flechaSom;
		private int flechaDif;
		private boolean escolheu;
		private Image sound;
		private Image difficulty;
		private BD banco;
		private short[] opcoes;

		public Setup() {
			banco = new BD("configuracoes",1);
			opcoes = banco.verificarConfiguracoes();
			opcaoEscolhidaSom = opcoes[2];
			opcaoEscolhidaDific = opcoes[3];
			escolheu = false;
			flechaSom = 1;
			flechaDif = 1;
			try {
				sound = Image.createImage("/SR_Menu/IM_Sound.png");
				difficulty = Image.createImage("/SR_Menu/IM_Difficulty.png");
			}catch(IOException e) { System.out.println("DEU PAU NA INICIALIZACAO DAS IMAGENS DO CONSTRUTOR DA SUB CLASSE SETUP"); }
			int[] seq = {0, 1};
			sperm.setFrameSequence(seq);
			sperm.setRefPixelPosition(-70, 0);
		}

		private void setarSom() {
			controleCanvas = 5;
			loop = true;
			while(loop) {
				if(tecla == Teclas.NAVEGACAOACIMA || tecla == Teclas.DOIS) {
					opcaoEscolhidaSom -= 1;
					if(opcaoEscolhidaSom < 1)
						opcaoEscolhidaSom = 4;
					flechaSom = 1;
					tecla = -2048;
				}
				else if(tecla == Teclas.NAVEGACAOABAIXO || tecla == Teclas.OITO) {
					opcaoEscolhidaSom += 1;
					if(opcaoEscolhidaSom > 4)
						opcaoEscolhidaSom = 1;
					flechaSom = 2;
					tecla = -2048;
				}
				else
					flechaSom = 3;
				if(tecla == Teclas.NAVEGACAOMEIO || tecla == Teclas.CINCO || tecla == Teclas.SOFTBUTTONESQUERDO) {
					//MANDAR PRA ALGUM LUGAR O VALOR ENCONTRADO: 0 = FACIL/1 = MEDIO/2 = DIFICIL
					escolheu = true;
					s.repaint();
					try {
						Thread.sleep(300);
					}catch(InterruptedException e){  }
					/* E aqui ele faz o tratamento do Volume, setando logo.
					 * 
					 */
					switch (opcaoEscolhidaSom) {
					case 1:
						musica.musicVolume(0);
						break;
					case 2:
						musica.musicVolume(33);
						break;
					case 3:
						musica.musicVolume(66);
						break;
					case 4:
						musica.musicVolume(100);
					}
					opcoes[2] = opcaoEscolhidaSom;
					banco.editarOpcoes(opcoes);
					banco.fechar();
					loop = false;
					tecla = -2048;
				}
				else if(tecla == Teclas.SOFTBUTTONDIREITO) {
					banco.fechar();
					loop = false;
					tecla = -2048;
				}
				sperm.nextFrame();
				s.repaint();
				try {
					Thread.sleep(100);
				}catch(InterruptedException e) {  }
			}
		}

		private void setarDific() {
			controleCanvas = 6;
			loop = true;
			while(loop) {
				if(tecla == Teclas.NAVEGACAOACIMA || tecla == Teclas.DOIS) {
					opcaoEscolhidaDific -= 1;
					if(opcaoEscolhidaDific < 1)
						opcaoEscolhidaDific = 3;
					flechaDif = 1;
					tecla = -2048;
				}
				else if(tecla == Teclas.NAVEGACAOABAIXO || tecla == Teclas.OITO) {
					opcaoEscolhidaDific += 1;
					if(opcaoEscolhidaDific > 3)
						opcaoEscolhidaDific = 1;
					flechaDif = 2;
					tecla = -2048;
				}
				else
					flechaDif = 3;
				if(tecla == Teclas.NAVEGACAOMEIO || tecla == Teclas.CINCO || tecla == Teclas.SOFTBUTTONESQUERDO) {
					//MANDAR PRA ALGUM LUGAR O VALOR ENCONTRADO: 0 = FACIL/1 = MEDIO/2 = DIFICIL
					escolheu = true;
					s.repaint();
					try {
						Thread.sleep(300);
					}catch(InterruptedException e) {  }
					opcoes[3] = opcaoEscolhidaDific;
					banco.editarOpcoes(opcoes);
					banco.fechar();
					loop = false;
					tecla = -2048;
				}
				else if(tecla == Teclas.SOFTBUTTONDIREITO) {
					banco.fechar();
					loop = false;
					tecla = -2048;
				}
				sperm.nextFrame();
				s.repaint();
				try {
					Thread.sleep(100);
				}catch(InterruptedException e) {  }
			}
		}

		public void paint(Graphics g) {
			
			int i=0,h=g.getClipHeight()/10;
			for(int r=200; r>=150;r-=5){
			   g.setColor(r,0,0);
			   g.fillRect(0, i*h, g.getClipWidth(), h);
			   i++;
			}
			g.setColor(255,255,255);
	//		g.drawImage(veias, 0, 0, Graphics.TOP|Graphics.LEFT);
			g.drawImage(logo,(g.getClipWidth()/2),-transformaA(22), Graphics.TOP|Graphics.HCENTER);
			g.drawImage(back, s.getWidth() - transformaL(6), s.getHeight() - transformaA(6),Graphics.BOTTOM|Graphics.RIGHT);
			g.drawImage(faixa, s.getWidth()/2, s.getHeight()-transformaA(85), Graphics.TOP|Graphics.HCENTER);
			g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_MEDIUM));
			g.drawImage(frase, s.getWidth()/2 + transformaL(2), s.getHeight() - transformaA(17), Graphics.TOP|Graphics.HCENTER);
			g.drawImage(ok,transformaL(6),s.getHeight() - transformaA(6),Graphics.BOTTOM|Graphics.LEFT);
			sperm.defineReferencePixel(transformaL(85), 0);
			sperm.setRefPixelPosition(s.getWidth()/2, s.getHeight()-transformaA(58));
			sperm.setTransform(Sprite.TRANS_NONE);
			sperm.paint(g);
			sperm.setTransform(Sprite.TRANS_MIRROR);
			sperm.paint(g);
			sperm.defineReferencePixel(transformaL(100), 0);
			sperm.setRefPixelPosition(s.getWidth()/2, s.getHeight()-transformaA(53));
			sperm.setTransform(Sprite.TRANS_NONE);
			sperm.paint(g);
			sperm.setTransform(Sprite.TRANS_MIRROR);
			sperm.paint(g);
			if(controleCanvas == 5) { 		//SETUP DO SOM
				g.drawImage(sound, s.getWidth()/2, transformaA(75), Graphics.TOP|Graphics.HCENTER);
				if(escolheu == true && opcaoEscolhidaSom == 1) {
					g.setColor(255, 222, 0);
					g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
				}
				else {
					g.setColor(255, 255, 255);
					g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_MEDIUM));
				}
				g.drawString("No Sound", s.getWidth()/2, s.getHeight()/2 + transformaA(-42), Graphics.BASELINE|Graphics.HCENTER);
				if(escolheu == true && opcaoEscolhidaSom == 2) {
					g.setColor(255, 222, 0);
					g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
				}
				else {
					g.setColor(255, 255, 255);
					g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_MEDIUM));
				}
				g.drawString("Low", s.getWidth()/2, s.getHeight()/2 + transformaA(-20), Graphics.BASELINE|Graphics.HCENTER);
				if(escolheu == true && opcaoEscolhidaSom == 3) {
					g.setColor(255, 222, 0);
					g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
				}
				else {
					g.setColor(255, 255, 255);
					g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_MEDIUM));
				}
				g.drawString("Medium", s.getWidth()/2, s.getHeight()/2 + transformaA(+2), Graphics.BASELINE|Graphics.HCENTER);
				if(escolheu == true && opcaoEscolhidaSom == 4) {
					g.setColor(255, 222, 0);
					g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
				}
				else {
					g.setColor(255, 255, 255);
					g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_MEDIUM));
				}
				g.drawString("High", s.getWidth()/2, s.getHeight()/2 + transformaA(24), Graphics.BASELINE|Graphics.HCENTER);
				switch(flechaSom) {
				case 1:
					g.drawImage(setaD, s.getWidth()/2 - transformaL(65), s.getHeight()/2 + transformaA(ySeta[opcaoEscolhidaSom - 1]), Graphics.HCENTER|Graphics.VCENTER);
					break;
				case 2:
					g.drawImage(setaD, s.getWidth()/2 - transformaL(65), s.getHeight()/2 + transformaA(ySeta[opcaoEscolhidaSom - 1]), Graphics.HCENTER|Graphics.VCENTER);
					break;
				case 3:
					g.drawImage(setaD, s.getWidth()/2 - transformaL(65), s.getHeight()/2 + transformaA(ySeta[opcaoEscolhidaSom - 1]), Graphics.HCENTER|Graphics.VCENTER);
					break;
				default:
					break;
				}
			}
			else if(controleCanvas == 6) {	//SETUP DA DIFICULDADE
				g.drawImage(difficulty, s.getWidth()/2, transformaA(75), Graphics.TOP|Graphics.HCENTER);
				if(escolheu == true && opcaoEscolhidaDific == 1) {
					g.setColor(255, 222, 0);
					g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
				}
				else {
					g.setColor(255, 255, 255);
					g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_MEDIUM));
				}
				g.drawString("Easy", s.getWidth()/2, s.getHeight()/2 + transformaA(-22), Graphics.BASELINE|Graphics.HCENTER);
				if(escolheu == true && opcaoEscolhidaDific == 2) {
					g.setColor(255, 222, 0);
					g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
				}
				else {
					g.setColor(255, 255, 255);
					g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_MEDIUM));
				}
				g.drawString("Medium", s.getWidth()/2, s.getHeight()/2 + transformaA(0), Graphics.BASELINE|Graphics.HCENTER);
				if(escolheu == true && opcaoEscolhidaDific == 3) {
					g.setColor(255, 222, 0);
					g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
				}
				else {
					g.setColor(255, 255, 255);
					g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_MEDIUM));
				}
				g.drawString("Hard", s.getWidth()/2, s.getHeight()/2 + transformaA(22), Graphics.BASELINE|Graphics.HCENTER);
				switch(flechaDif) {
				case 1:
					g.drawImage(setaD, s.getWidth()/2 - transformaL(60), s.getHeight()/2 + transformaA(xSeta[opcaoEscolhidaDific - 1]), Graphics.VCENTER|Graphics.HCENTER);
					break;
				case 2:
					g.drawImage(setaD, s.getWidth()/2 - transformaL(60), s.getHeight()/2 + transformaA(xSeta[opcaoEscolhidaDific - 1]), Graphics.VCENTER|Graphics.HCENTER);
					break;
				case 3:
					g.drawImage(setaD, s.getWidth()/2 - transformaL(60), s.getHeight()/2 + transformaA(xSeta[opcaoEscolhidaDific - 1]), Graphics.VCENTER|Graphics.HCENTER);
					break;
				default:
					break;
				}
			}
		}
	}
}
