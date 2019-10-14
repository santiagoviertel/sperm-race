package SR_Race;

import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;
import javax.microedition.lcdui.Image;
import java.io.IOException;

public class Inimigos {

	private Diafragma d;
	private Exterminador e;

	//Parametros: coor da pos. inicial, tempo de animacao - troca de frame, tempo de animacao - translacao, coor da pos final
	//Parametros: coor da pos. inicial, tempo de animacao - troca de frame, tempo de animacao - translacao, coor da pos final, alcance do tiro
	public Inimigos(int xd, int yd, int zd, int wd, int fimXd, int fimYd,
			int xe, int ye, int ze, int we, int fimXe, int fimYe, int alc) {
		d = new Diafragma(xd,yd,zd,wd,fimXd,fimYd);
		e = new Exterminador(xe,ye,ze,we,fimXe,fimYe,alc);
	}

	public void adicionaCamada(GerenciadorCamadas lm) {
		lm.adiciona(d.retornaSprite());
		lm.adiciona(e.retornaSprite());
		e.adicionaCamadaEspermicida(lm);
	}

	public void atualizaInimigo(Sprite jogador,int deltaTempo) {
		d.atualizaInimigo(deltaTempo);
		e.atualizaInimigo(jogador,deltaTempo);
	}

	public Sprite retornaDiafragma() {
		return d.retornaSprite();
	}

	public Sprite retornaExterminador() {
		return e.retornaSprite();
	}

	public TiledLayer retornaEspermicida() {
		return e.retornaTiledLayer();
	}

	public float retornaVelocidadeDiafragma() {
		return d.retornaVelocidade();
	}

	public float retornaVelocidadeExterminador() {
		return e.retornaVelocidade();
	}

	private class Diafragma {

		private Sprite inimigo;
		private float velocidade;
		private int tempoFrame;
		private int deltaFrame;
		private int[] pontoI;
		private int[] pontoF;
		private int tempoAnima;
		private int deltaAnima;
		private boolean flag;

		private Diafragma(int x, int y, int z, int w, int fimX, int fimY) {
			try {
				inimigo = new Sprite(Image.createImage("/SR_Race/IM_Diaphragm.png"), 54, 32);
			}catch(IOException e){ System.out.println("ERRO NA CRIACAO DO DIAFRAGMA - CLASSE INIMIGO"); }
			int[] diafragma = {0, 1};
			inimigo.setFrameSequence(diafragma);
			inimigo.setFrame(0);
			pontoI = new int[2];
			pontoF = new int[2];
			pontoI[0] = x;
			pontoI[1] = y;
			pontoF[0] = fimX;
			pontoF[1] = fimY;
			tempoFrame = z;
			tempoAnima = w;
			inimigo.setPosition(x, y);
			inimigo.defineReferencePixel(inimigo.getWidth()/2,inimigo.getHeight()/2);
			deltaFrame = 0;
			deltaAnima = 0;
			if(w==0)
				velocidade = 0;
			else
				velocidade = (fimX - x)/(w/1000);
			flag = false;
		}

		private void atualizaInimigo(int deltaTempo) {
			int posX,posY;
			int moduloX = pontoF[0] - pontoI[0];
			int moduloY = pontoF[1] - pontoI[1];
			//movimentação do diafragma
			posX = pontoI[0] + (moduloX*deltaAnima)/(tempoAnima/2);
			posY = pontoI[1] + (moduloY*deltaAnima)/(tempoAnima/2);
			if(deltaAnima >= tempoAnima)
				deltaAnima -= tempoAnima;
			if((deltaAnima <= tempoAnima/2) && (!flag))
				deltaAnima += deltaTempo;
			else {
				deltaAnima -= deltaTempo;
				if(!flag)
					velocidade = -velocidade;
				flag = true;
				if(deltaAnima <= 0) {
					velocidade = -velocidade;
					flag = false;
					deltaAnima = 0;
				}
			}
			inimigo.setPosition(posX, posY);
			deltaFrame += deltaTempo;
			if(deltaFrame >= tempoFrame) {
				deltaFrame -= tempoFrame;
				inimigo.nextFrame();
			}
		}

		private Sprite retornaSprite() {
			return inimigo;
		}

		private float retornaVelocidade() {
			return velocidade;
		}
	}

	private class Exterminador {

		private Sprite tiro;
		private int estado;
		private int[] tiroPI;
		private int dist;
		private int deltaAnimaT;
		private TiledLayer espermicida;
		private Sprite inimigo;
		private float velocidade;
		private int tempoFrame;
		private int deltaFrame;
		private int[] pontoI;
		private int[] pontoF;
		private int tempoAnima;
		private int deltaAnima;
		private boolean flag;

		private Exterminador(int x, int y, int z, int w, int fimX, int fimY, int alcance) {
			//Matriz do Espermicida
			int[][] esper = {
					{0, 4, 4, 0},
					{2, 1, 1, 3},
					{2, 1, 1, 3},
					{2, 1, 1, 3},
					{0, 5, 5, 0}
			};
			try {
				inimigo = new Sprite(Image.createImage("/SR_Race/IM_Exterminator.png"), 46, 34);
				tiro = new Sprite(Image.createImage("/SR_Race/IM_HitSpermicidal.png"),13,13);
				espermicida = new TiledLayer(esper[0].length,esper.length,Image.createImage("/SR_Race/IM_Spermicidal.png"),22,22);
			}catch(IOException e){ System.out.println("ERRO NA CRIACAO DO EXTERMINADOR - CLASSE INIMIGO"); }
			int i,j;
			for(i=0;i<esper.length;i++)
				for(j=0;j<esper[0].length;j++)
					espermicida.setCell(j,i, esper[i][j]);
			tiroPI = new int[2];
			tiroPI[0] = x + 24;
			tiroPI[1] = y + 15;
			dist = alcance;
			espermicida.setPosition((tiroPI[0]+dist)-27, tiroPI[1]-27);
			espermicida.setVisible(false);
			int[] killer = {0,3,4};
			int[] killershot = {2,1,0};
			inimigo.setFrameSequence(killer);
			inimigo.setFrame(0);
			inimigo.setTransform(Sprite.TRANS_MIRROR);
			tiro.setFrameSequence(killershot);
			tiro.setFrame(0);
			tiro.setTransform(Sprite.TRANS_MIRROR);
			tiro.setVisible(true);
			tiro.setRefPixelPosition(tiro.getWidth()/2, tiro.getHeight()/2);
			pontoI = new int[2];
			pontoF = new int[2];
			pontoI[0] = x;
			pontoI[1] = y;
			pontoF[0] = fimX;
			pontoF[1] = fimY;
			tempoFrame = z;
			tempoAnima = w;
			inimigo.setPosition(x, y);
			inimigo.defineReferencePixel(inimigo.getWidth()/2,inimigo.getHeight()/2);
			deltaFrame = 0;
			deltaAnima = 0;
			deltaAnimaT = 0;
			flag = false;
			estado = 0;
			if(w==0)
				velocidade = 0;
			else
				velocidade = (fimX - x)/(w/1000);
			flag = false;
		}

		private TiledLayer retornaTiledLayer() {
			return espermicida;
		}

		private void animaInimigo(int deltaTempo) {
			int posX,posY;
			int moduloX = pontoF[0] - pontoI[0];
			int moduloY = pontoF[1] - pontoI[1];
			int[] frameS1 = {0,1,2};
			switch(estado) {
			case 1:
				inimigo.nextFrame();
				break;
			case 2:
				if(flag==false) {
					inimigo.setFrameSequence(frameS1);
					inimigo.setFrame(0);
					flag = true;
				}
				inimigo.nextFrame();
				break;
			case 4:
				posX = pontoI[0] + (moduloX*deltaAnima)/(tempoAnima);
				posY = pontoI[1] + (moduloY*deltaAnima)/(tempoAnima);
				if(deltaAnima >= tempoAnima)
					deltaAnima -= tempoAnima;
				deltaAnima += deltaTempo;
				inimigo.setPosition(posX, posY);
				if(posX >= pontoF[0] && posY >= pontoF[1]) {
					estado = 5;
					inimigo.setVisible(false);
				}
				else {
					deltaFrame += deltaTempo;
					if(deltaFrame >= tempoFrame) {
						deltaFrame -= tempoFrame;
						inimigo.nextFrame();
					}
				}
			}
		}

		private void animaTiro(int deltaTempo){
			int posXT;
			int moduloXT = (tiroPI[0]+dist) - tiroPI[0];
			switch(estado) {
			case 1:
				inimigo.nextFrame();
				estado = 2;
				break;
			case 2:
				posXT = (tiroPI[0]) + (moduloXT*(deltaAnimaT*3))/(50);
				if(deltaAnimaT >= 50)
					deltaAnimaT -= 50;
				deltaAnimaT += deltaTempo;
				tiro.setPosition(posXT, tiroPI[1]);
				if(posXT >= (tiroPI[0]+dist)/2 && tiro.getFrame()!=1) {
					tiro.nextFrame();
				}else if (posXT >= (tiroPI[0]+dist)&& tiro.getFrame()!=2) {
					tiro.nextFrame();
					tiro.setPosition(tiroPI[0]+dist, tiroPI[1]);
					estado = 3;
					flag = false;
				}
				break;
			case 3:
				espermicida.setVisible(true);
				tiro.setVisible(false);
				estado = 4;
			}
		}

		private void atualizaInimigo(Sprite jogador,int deltaTempo) {
			if(estado==0) {
				int cx = jogador.getRefPixelX();
				int cy = jogador.getRefPixelY();
				int ex = inimigo.getRefPixelX();
				int ey = inimigo.getRefPixelY();

				float distancia = (float)Math.sqrt((cx-ex)*(cx-ex)+(cy-ey)*(cy-ey));
				if(distancia<=100)
					estado = 1;
			}
			else {
				animaInimigo(deltaTempo);
				animaTiro(deltaTempo);
			}
		}

		private Sprite retornaSprite() {
			return inimigo;
		}

		private float retornaVelocidade() {
			return velocidade;
		}

		private void adicionaCamadaEspermicida(GerenciadorCamadas lm) {
			lm.adiciona(tiro);
			lm.adiciona(espermicida);
		}
	}
}