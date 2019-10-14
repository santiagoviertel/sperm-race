package SR_Race;

import javax.microedition.lcdui.game.TiledLayer;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import java.io.IOException;

public class Pista {

	private static final int TEMPO_NITRO = 160;

	private TiledLayer caminho;
	private TiledLayer fundo;
	private TiledLayer pistaMenor;
	private TiledLayer bandeiraQuadriculada;
	private short[][][] checkpoint;
	private short[] porcentagemPista;
	private short[] pontoInicialPorcentagem;
	private short[] pontoInicial;
	private Sprite[] placas;
	private boolean voltas;
	private Sprite[] nitros;
	private int deltaTempoNitros;
	private int tempoMedioPista;
	private Inimigos ini;

	
	public Pista(int escolha,int w,int h) {
		try{
			bandeiraQuadriculada = new TiledLayer(5,1,Image.createImage("/SR_Race/IM_FinalFlag.png"),20,20);

			int x,y;
			int i,j;
			x = w/22;
			if(w%22>0)
				x += 1;
			y = h/22;
			if(h%22>0)
				y += 1;
			x += 1;
			y += 1;

			
			{
				Image i1 = Image.createImage("/SR_Race/IM_TilesUterus.png");
				{
					byte[][] pista =  pista4();
					caminho = new TiledLayer(pista[0].length, pista.length, i1,22,22);
					for(i = 0; i < pista.length; i+=1)
						for(j = 0; j < pista[0].length; j+=1)
							caminho.setCell(j, i, pista[i][j]);
				}
				System.gc();
				pistaMenor = new TiledLayer(x,y,i1,22,22);
				fundo = new TiledLayer(x,y,i1,22,22);
				for(i = 0; i < y; i+=1)
					for(j = 0; j < x; j+=1)
						fundo.setCell(j, i, 14);
				for(i = 0; i < 5; i+=1)
					bandeiraQuadriculada.setCell(i, 0, 1);
				pistaMenor.setPosition(Integer.MIN_VALUE,Integer.MIN_VALUE);
			}
			System.gc();
			{	
				Image i2 = Image.createImage("/SR_Race/IM_Signs.png");
				int[][] posicoes_placas = posicoesPlacas4();
				int[] indices_placas = indicesPlacas4();
				placas = new Sprite[posicoes_placas.length];
				for(i = 0;i < posicoes_placas.length;i += 1) {
					placas[i] = new Sprite(i2,22,22);
					placas[i].setFrame(indices_placas[i]%2);
					placas[i].setTransform(indices_placas[i]/2);
					placas[i].setPosition(posicoes_placas[i][0],posicoes_placas[i][1]);
				}
			}
			System.gc();
			{
				Image i3 = Image.createImage("/SR_Race/IM_Nitro.png");
				int[][] nitro = nitro4();
				nitros = new Sprite[nitro.length];
				for(i=0;i<nitro.length;i+=1) {
					nitros[i] = new Sprite(i3,12,20);
					nitros[i].setPosition(nitro[i][0],nitro[i][1]);
				}
			}
			System.gc();
			checkpoint = checkpoint4();
			pontoInicial = pontoPartida4();
			ini = criaInimigo4();
			porcentagemPista = porcentagem4();
			pontoInicialPorcentagem = inicioPorcentagem4();
			tempoMedioPista = SR_Menu.BD.PISTA4;
			bandeiraQuadriculada.setPosition((int)((checkpoint[porcentagemPista[porcentagemPista.length - 1]][0][0] + checkpoint[porcentagemPista[porcentagemPista.length - 1]][1][0])/2 - bandeiraQuadriculada.getCellWidth()*2.5),checkpoint[porcentagemPista[porcentagemPista.length - 1]][0][1]);
			voltas = false;
			
			
			deltaTempoNitros = 0;
		}catch(IOException p){ 		System.out.println("ERRO N" + "A TILEDLAYER OU NA IMAGE - CLASSE PISTA"); }
		//Carrega a Tiled
		System.gc();
	}

	public Inimigos retornaInimigos() {
		return ini;
	}

	public boolean fazVoltas() {
		return voltas;
	}

	public TiledLayer retornaCamadaDeTiles() {
		return caminho;
	}

	public void adicionaCamada(GerenciadorCamadas lm) {
		int i;
		if(ini!=null)
			ini.adicionaCamada(lm);
		for(i=0;i<nitros.length;i+=1)
			lm.adiciona(nitros[i]);
		for(i=0;i<placas.length;i+=1)
			lm.adiciona(placas[i]);
		lm.adiciona(pistaMenor);
		lm.adiciona(bandeiraQuadriculada);
		lm.adiciona(fundo);
	}

	public void atualizaPista(Sprite primeiro,int deltaTempo,int x,int y) {
		if(ini!=null)
			ini.atualizaInimigo(primeiro,deltaTempo);
		deltaTempoNitros += deltaTempo;
		if(deltaTempoNitros>=TEMPO_NITRO) {
			deltaTempoNitros -= TEMPO_NITRO;
			for(int i=0;i<nitros.length;i+=1)
				nitros[i].prevFrame();
		}
		int posX,posY,celX,celY;
		if(x<0) {
			posX = x - (caminho.getCellWidth() - Math.abs(x%caminho.getCellWidth()));
			celX = x/caminho.getCellWidth() - 1;
		}
		else {
			posX = x - Math.abs(x%caminho.getCellWidth());
			celX = x/caminho.getCellWidth();
		}
		if(y<0) {
			posY = y - (caminho.getCellHeight() - Math.abs(y%caminho.getCellHeight()));
			celY = y/caminho.getCellHeight() - 1;
		}
		else {
			posY = y - Math.abs(y%caminho.getCellHeight());
			celY = y/caminho.getCellHeight();
		}
		if(pistaMenor.getX()!=posX||pistaMenor.getY()!=posY) {
			fundo.setPosition(posX,posY);
			pistaMenor.setPosition(posX,posY);
			for(int i=0;i<pistaMenor.getRows();i+=1)
				for(int j=0;j<pistaMenor.getColumns();j+=1) {
					if(celX+j>=0&&celX+j<caminho.getColumns()&&celY+i>=0&&celY+i<caminho.getRows())
						pistaMenor.setCell(j,i,caminho.getCell(celX+j,celY+i));
					else
						pistaMenor.setCell(j,i,1);
				}
		}
	}

	public boolean pegouNitro(Sprite s) {
		for(int i=0;i<nitros.length;i+=1)
			if(s.collidesWith(nitros[i],false))
				return true;
		return false;
	}

	public short[] inicioPorcentagemPista() {
		return pontoInicialPorcentagem;
	}

	public short[] retornaPorcentagemPista() {
		return porcentagemPista;
	}

	public short[][] retornaCheckpoint(short x) {
		return checkpoint[x];
	}

	public int retornaMediaTempo() {
		return tempoMedioPista;
	}

	public void pintaBasePlacas(Graphics g,int x,int y,int w,int h) {
		for(int i=0;i<placas.length;i+=1)
			if((placas[i].getX() + 8<=x + w)&&(placas[i].getX() + 8 + 6>x))
				if((placas[i].getY() + 22<=y + h)&&(placas[i].getY() + 22 + 15>y)) {
					g.setColor(110,55,0);
					g.drawRect(placas[i].getX() - x + 8,placas[i].getY() - y + 22,6,15);
					g.setColor(180,110,20);
					g.fillRect(placas[i].getX() - x + 9,placas[i].getY() - y + 23,5,14);
				}
	}

	public int retornaOrientacaoInicial() {
		switch(pontoInicial[2]) {
		case 0:
			return 4;
		case 1:
			return 0;
		case 2:
			return 12;
		}
		return 8;
	}

	public int[][] pontosPartida() {
		int[][] partida;
		partida = new int[4][2];
		switch(pontoInicial[2]) {
		case 0:
			partida[0][0] = pontoInicial[0] - 15;
			partida[0][1] = pontoInicial[1] - 15;

			partida[1][0] = pontoInicial[0] + 15;
			partida[1][1] = pontoInicial[1] - 15;

			partida[2][0] = pontoInicial[0] - 15;
			partida[2][1] = pontoInicial[1] + 15;

			partida[3][0] = pontoInicial[0] + 15;
			partida[3][1] = pontoInicial[1] + 15;
			break;
		case 1:
			partida[0][0] = pontoInicial[0] + 15;
			partida[0][1] = pontoInicial[1] - 15;

			partida[1][0] = pontoInicial[0] + 15;
			partida[1][1] = pontoInicial[1] + 15;

			partida[2][0] = pontoInicial[0] - 15;
			partida[2][1] = pontoInicial[1] - 15;

			partida[3][0] = pontoInicial[0] - 15;
			partida[3][1] = pontoInicial[1] + 15;
			break;
		case 2:
			partida[0][0] = pontoInicial[0] + 15;
			partida[0][1] = pontoInicial[1] + 15;

			partida[1][0] = pontoInicial[0] - 15;
			partida[1][1] = pontoInicial[1] + 15;

			partida[2][0] = pontoInicial[0] + 15;
			partida[2][1] = pontoInicial[1] - 15;

			partida[3][0] = pontoInicial[0] - 15;
			partida[3][1] = pontoInicial[1] - 15;
			break;
		case 3:
			partida[0][0] = pontoInicial[0] - 15;
			partida[0][1] = pontoInicial[1] + 15;

			partida[1][0] = pontoInicial[0] - 15;
			partida[1][1] = pontoInicial[1] - 15;

			partida[2][0] = pontoInicial[0] + 15;
			partida[2][1] = pontoInicial[1] + 15;

			partida[3][0] = pontoInicial[0] + 15;
			partida[3][1] = pontoInicial[1] - 15;
			break;
		}
		return partida;
	}

	//matrizes da pista 4
	private byte[][] pista4() {
		byte matriz[][] = {
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 5,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1,11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1,11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6,12, 7, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1,11, 0, 0, 0, 0, 0, 0, 0, 0, 0,10, 1,11, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1,11, 0, 0, 0, 0, 0, 0, 0, 0, 0,10, 1,11, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1,11, 0, 0, 0, 0, 6,12,12,12,12, 2, 1, 3, 7, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1,11, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1,11, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1,11, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1,11, 0, 0, 0, 6, 2, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1,11, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1,11, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1,11, 0, 0, 0, 8, 4, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1,11, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1,11, 0, 0, 0, 0, 8,13, 4, 1, 1, 1, 1, 1,11, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1,11, 0, 0, 0, 0, 0, 0,10, 1, 1, 1, 1, 1,11, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1,11, 0, 0, 0, 0, 0, 0, 8, 4, 1, 1, 1, 1, 3,12, 7, 0, 8,13,13,13,13,13,13,13,13,13, 4, 1},
				{1,11, 0, 0, 0, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1,11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10, 1},
				{1,11, 0, 0, 0, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1,11, 0, 0, 6,12, 7, 0, 0, 0, 0, 0, 0,10, 1},
				{1, 3,12, 7, 0, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1,11, 0, 0,10, 1,11, 0, 0, 0, 0, 0, 0,10, 1},
				{1, 1, 1,11, 0, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 3,12,12, 2, 1,11, 0, 0, 0, 0, 0, 0,10, 1},
				{1, 1, 1,11, 0, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0, 0, 0, 0,10, 1},
				{1, 1, 1,11, 0, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0, 0, 0, 0,10, 1},
				{1, 1, 1,11, 0, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0, 0, 0, 0,10, 1},
				{1, 1, 1,11, 0, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3,12,12,12, 7, 0, 0,10, 1},
				{1, 1, 1, 3,12, 7, 0, 6,12, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 6, 2, 1},
				{1, 1, 1, 1, 1,11, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0,10, 1, 1},
				{1, 1, 1, 1, 1,11, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 5,13,13, 9, 0,10, 1, 1},
				{1, 1, 1, 1, 1,11, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0, 0,10, 1, 1},
				{1, 1, 1, 1, 1,11, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 6,12,12, 2, 1, 1},
				{1, 1, 1, 1, 1, 3,12, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0,10, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0,10, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 8,13,13,13, 4, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0, 0, 0,10, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3,12,12,12, 7, 0,10, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0,10, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0,10, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 9, 0,10, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0,10, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 5,13, 9, 0, 0,10, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 5,13,13,13,13, 4, 1, 1, 1, 1,11, 0, 0, 0, 0,10, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0, 0,10, 1, 1, 5,13, 9, 0, 0, 0, 0,10, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0, 0, 8,13,13, 9, 0, 0, 0, 0, 0, 0,10, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0, 6,12,12,12,12,12,12,12,12,12,12, 2, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 7, 0, 6, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0,10, 1, 1, 1, 1, 5,13,13,13,13,13,13, 4, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 9, 0, 8, 4, 1, 1, 1,11, 0, 0, 0, 0, 0, 0,10, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0, 0,10, 1, 5,13, 9, 0, 0, 0, 0, 0, 0,10, 1},
				{1, 5,13,13,13,13,13,13,13,13, 4, 1, 1,11, 0, 0, 0, 8,13, 9, 0, 0, 0, 0, 0, 0, 0, 0,10, 1},
				{1,11, 0, 0, 0, 0, 0, 0, 0, 0,10, 1, 1,11, 0, 0, 0, 0, 0, 0, 0, 6,12, 7, 0, 0, 0, 0,10, 1},
				{1,11, 0, 6,12,12, 7, 0, 0, 0, 8, 4, 1, 3,12,12,12,12,12,12,12, 2, 1, 3,12, 7, 0, 0,10, 1},
				{1,11, 0,10, 1, 1, 3, 7, 0, 0, 0, 8, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0,10, 1},
				{1,11, 0,10, 1, 1, 1,11, 0, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0,10, 1},
				{1,11, 0,10, 1, 1, 1, 3, 7, 0, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0,10, 1},
				{1,11, 0,10, 1, 1, 1, 1, 3, 7, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0,10, 1},
				{1,11, 0,10, 1, 1, 1, 1, 1,11, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0,10, 1},
				{1,11, 0, 8,13, 4, 1, 1, 1,11, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0,10, 1},
				{1,11, 0, 0, 0,10, 1, 1, 1,11, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0,10, 1},
				{1,11, 0, 0, 0,10, 1, 1, 5, 9, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0,10, 1},
				{1,11, 0, 6,12, 2, 1, 1,11, 0, 0, 6, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0,10, 1},
				{1,11, 0,10, 1, 1, 1, 1,11, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0,10, 1},
				{1,11, 0,10, 1, 1, 1, 1,11, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 7, 0,10, 1},
				{1,11, 0,10, 1, 1, 1, 1,11, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0,10, 1},
				{1,11, 0, 8,13, 4, 1, 1,11, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0,10, 1},
				{1,11, 0, 0, 0,10, 1, 1, 3, 7, 0, 8, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0,10, 1},
				{1,11, 0, 0, 0,10, 1, 1, 1,11, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0,10, 1},
				{1,11, 0, 0, 0,10, 1, 1, 1,11, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 9, 0,10, 1},
				{1,11, 0, 0, 0,10, 1, 1, 1,11, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0,10, 1},
				{1,11, 0, 0, 0,10, 1, 1, 1,11, 0, 0,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 0, 0,10, 1},
				{1,11, 0, 0, 0,10, 1, 1, 5, 9, 0, 0, 8,13,13,13,13,13, 4, 1, 5,13,13,13,13, 9, 0, 0,10, 1},
				{1,11, 0, 0, 0,10, 1, 1,11, 0, 0, 0, 0, 0, 0, 0, 0, 0,10, 1,11, 0, 0, 0, 0, 0, 0, 0,10, 1},
				{1,11, 0, 0, 0,10, 1, 1,11, 0, 0, 0, 0, 6,12,12, 7, 0, 8,13, 9, 6,12,12, 7, 0, 0, 0,10, 1},
				{1, 3,12,12,12, 2, 1, 1,11, 0, 0, 0, 0,10, 1, 1,11, 0, 0, 0, 0,10, 1, 1,11, 0, 0, 0,10, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 3,12,12,12,12, 2, 1, 1, 3,12,12,12,12, 2, 1, 1, 3,12,12,12, 2, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
		};
		return matriz;
	}

	private short[][][] checkpoint4() {
		short[][][] matriz = {
				//Ponto 1, Ponto 2, Checkpoints Anteriores, Proximos Checkpoints
				{{51,1538},{61,1538},{},{1}},
				{{51,1473},{61,1473},{0},{2}},
				{{51,1388},{61,1388},{1},{3}},
				{{51,1278},{61,1278},{2},{4}},
				{{91,1238},{91,1248},{3},{5}},
				{{126,1238},{126,1248},{4},{6}},
				{{206,1278},{201,1283},{5},{7}},
				{{246,1318},{231,1333},{6},{8}},
				{{241,1368},{231,1368},{7},{9}},
				{{246,1423},{236,1423},{8},{10}},
				{{226,1473},{211,1473},{9},{11}},
				{{226,1568},{221,1568},{10},{12}},
				{{241,1583},{231,1583},{11},{13}},
				{{246,1668},{231,1668},{12},{14}},
				{{286,1698},{286,1708},{13},{15}},
				{{311,1698},{311,1708},{14},{16}},
				{{356,1698},{356,1708},{15},{17}},
				{{416,1748},{416,1753},{16},{18}},
				{{441,1733},{441,1738},{17},{19}},
				{{486,1703},{486,1708},{18},{20}},
				{{526,1703},{526,1713},{19},{21}},
				{{551,1703},{551,1713},{20},{22}},
				{{591,1668},{601,1668},{21},{23}},
				{{596,1603},{606,1603},{22},{24}},
				{{596,1518},{606,1518},{23},{25,26}},
				{{571,1483},{581,1483},{24},{27}},
				{{601,1483},{606,1483},{24},{27}},
				{{586,1278},{601,1278},{25,26},{28}},
				{{551,1238},{551,1228},{27},{29}},
				{{511,1233},{511,1223},{28},{30}},
				{{486,1228},{486,1218},{29},{31}},
				{{466,1243},{466,1238},{30},{32}},
				{{416,1248},{416,1238},{31},{33}},
				{{401,1238},{401,1228},{32},{34}},
				{{351,1208},{361,1208},{33},{35}},
				{{336,1163},{346,1163},{34},{36}},
				{{336,1128},{341,1128},{35},{37,38}},
				{{321,1093},{326,1093},{36},{39}},
				{{376,1093},{381,1093},{36},{39}},
				{{336,1013},{346,1013},{37,38},{40}},
				{{396,968},{396,978},{39},{41}},
				{{416,973},{416,983},{40},{42}},
				{{466,968},{466,978},{41},{43}},
				{{506,953},{506,978},{42},{44}},
				{{551,913},{551,978},{43},{45}},
				{{596,828},{606,828},{44},{46}},
				{{596,793},{606,793},{45},{47}},
				{{571,768},{571,758},{46},{48}},
				{{556,768},{556,753},{47},{49}},
				{{511,728},{521,728},{48},{50}},
				{{506,683},{516,683},{49},{51}},
				{{546,638},{546,648},{50},{52}},
				{{571,618},{581,618},{51},{53}},
				{{576,573},{591,573},{52},{54,55}},
				{{566,563},{571,563},{53},{56}},
				{{617,563},{622,563},{53},{56}},
				{{466,413},{466,403},{54,55},{57}},
				{{441,413},{441,403},{56},{58}},
				{{381,393},{391,388},{57},{59}},
				{{376,368},{386,368},{58},{60}},
				{{346,158},{366,158},{59},{61}},
				{{286,63},{286,53},{60},{62}},
				{{266,63},{266,53},{61},{63}},
				{{156,103},{156,78},{62},{64}},
				{{106,158},{71,158},{63},{65,66}},
				{{71,208},{66,208},{64},{67}},
				{{137,208},{132,208},{64},{67}},
				{{96,243},{66,243},{65,66},{68}},
				{{96,283},{61,283},{67},{69}},
				{{176,458},{111,458},{68},{70}},
				{{151,598},{136,598},{69},{71}},
				{{146,678},{141,678},{70},{}}
		};
		return matriz;
	}

	private short[] pontoPartida4() {
		short[] partida = {71,1698,0};
		return partida;
	}

	private short[] inicioPorcentagem4() {
		short[] resp = {79,1763};
		return resp;
	}

	private int[][] nitro4(){
		int[][] resp = {{96,1248},{391,1753},{571,1473},{381,1083},{621,543},{136,203}};
		return resp;
	}

	private short[] porcentagem4() {
		short[] resp = {
				0,1,2,3,4,5,6,7,8,9,10,11,12,
				13,14,15,16,17,18,19,20,21,22,
				23,24,27,28,29,30,31,32,33,34,
				35,36,39,40,41,42,43,44,45,46,
				47,48,49,50,51,52,53,56,57,58,
				59,60,61,62,63,64,67,68,69,70,71};
		return resp;
	}

	private int[][] posicoesPlacas4() {
		int[][] resp = {{86,1348},{286,1588},{491,1638},{531,1368},{426,1153},{446,913},{531,578},{486,343},{291,223},{186,153}};
		return resp;
	}

	private int[] indicesPlacas4() {
		int[] resp = {5,6,10,0,9,10,0,8,0,12};
		return resp;
	}

	private Inimigos criaInimigo4() {
		return new Inimigos(554,1426,200,1000,578,1426,
				279,194,60,2000,441,194,60);
	}

	
}