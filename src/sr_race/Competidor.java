package SR_Race;

import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Image;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

public class Competidor{

	private static final float SIN_45 = (float)(Math.sqrt(2)/2);
	private static final float[][] DIRECOES_QUADRANTE = {{1,0}, {(float)Math.cos(Math.PI/8),(float)Math.sin(Math.PI/8)}, {(float)Math.cos(Math.PI/4),(float)Math.sin(Math.PI/4)}, {(float)Math.cos(3*(Math.PI/8)),(float)Math.sin(3*(Math.PI/8))}};
	private static final float VELOCIDADE_MAXIMA_SEM_NITRO = 80;
	private static final float VELOCIDADE_COM_NITRO = 120;
	private static final float ACELERACAO_SEM_NITRO = 27;
	private static final float ACELERACAO_COM_NITRO = 6*ACELERACAO_SEM_NITRO;
	private static final float VELOCIDADE_ESPERMICIDA = 30;
	private static final float ACELERACAO_ESPERMICIDA = ACELERACAO_SEM_NITRO*0.5f;

	protected static  Image im_corpo;
	protected static  Image im_cauda;
	
	private Sprite corpo;
	private Sprite cauda;
	private int deltaTMudFrmCalda;
	private float deltaSMudFrmCorpo;
	private byte personagem;
	private int[] posicao_inicial;
	private float[] orientacao;
	private float[] velocidade_inicial;
	private float[] velocidade;
	private float[] aceleracao;
	private int[] tempo;
	private int tempoNitro;
	private boolean nitro;
	private boolean colisaoPista;
	private boolean colisaoEspermicida;  //Colisão no espermicida está setado
	private long tempoFinalizacao;
	//Variaveis responsaveis pela manipulacao dos checkpoints e porcentagem percorrida
	private int contCheckpoint;
	private float distanciaMaximaPontoCentral; //Distancia entre um checkpoint e outro
	private int[] pontoCentralContagemCheckpoint;  //Ponto do centro do proximo checkpoint que conta como porcentagem
	private float[] pontoCheckpoint;        //O ponto onde o agente busca
	private short[][][] ckDep;
	private short[] iCkDep;
	private short[][][] ckAnt;
	private short[] iCkAnt;
	private Random r;
	//Variaveis responsáveis pela contagem de voltas
	private boolean flagContaVolta;
	private int contVoltas;

	public Competidor(int x,int y,int ang,byte pers,Pista pista) {
		posicao_inicial = new int[2];
		orientacao = new float[2];
		velocidade_inicial = new float[2];
		velocidade = new float[2];
		aceleracao = new float[2];
		tempo = new int[2];
		deltaTMudFrmCalda = 0;
		deltaSMudFrmCorpo = 0;
		personagem = pers;
		tempoNitro = 0;
		nitro = false;
		colisaoPista = false;
		try {
			if(im_corpo==null || im_cauda==null){
				im_corpo = Image.createImage("/SR_Race/IM_Characters.png");
				im_cauda = Image.createImage("/SR_Race/IM_Tails.png"); 
			}
			corpo = new Sprite(im_corpo,22,25);
			cauda = new Sprite(im_cauda,24,28);
			corpo.defineReferencePixel(corpo.getWidth()/2,corpo.getHeight()/2);
			corpo.setRefPixelPosition(x,y);
		}catch(IOException e) { }
		mudaOrientacao(ang);
		velocidade_inicial[0] = 0;
		velocidade_inicial[1] = 0;
		velocidade[0] = velocidade_inicial[0];
		velocidade[1] = velocidade_inicial[1];
		contCheckpoint = 0;
		ckDep = new short[1][][];
		ckDep[0] = pista.retornaCheckpoint(pista.retornaPorcentagemPista()[0]);
		iCkDep = new short[1];
		iCkDep[0] = pista.retornaPorcentagemPista()[0];
		pontoCheckpoint = new float[2];
		pontoCentralContagemCheckpoint = new int[2];
		pontoCentralContagemCheckpoint[0] = (ckDep[0][1][0] + ckDep[0][0][0])/2;
		pontoCentralContagemCheckpoint[1] = (ckDep[0][1][1] + ckDep[0][0][1])/2;
		if(pista.fazVoltas()) {
			ckAnt = new short[ckDep[0][2].length][][];
			for(int i=0;i<ckDep[0][2].length;i+=1)
				ckAnt[i] = pista.retornaCheckpoint(ckDep[0][2][i]);
			int[] pontoCentralAnt = {(ckAnt[0][0][0] + ckAnt[0][1][0])/2,(ckAnt[0][0][1] + ckAnt[0][1][1])/2};
			distanciaMaximaPontoCentral = (float)Math.sqrt((pontoCentralAnt[0] - pontoCentralContagemCheckpoint[0])*(pontoCentralAnt[0] - pontoCentralContagemCheckpoint[0]) + (pontoCentralAnt[1] - pontoCentralContagemCheckpoint[1])*(pontoCentralAnt[1] - pontoCentralContagemCheckpoint[1]));
		}
		else {
			ckAnt = null;
			distanciaMaximaPontoCentral = (float)Math.sqrt((pista.inicioPorcentagemPista()[0] - pontoCentralContagemCheckpoint[0])*(pista.inicioPorcentagemPista()[0] - pontoCentralContagemCheckpoint[0]) + (pista.inicioPorcentagemPista()[1] - pontoCentralContagemCheckpoint[1])*(pista.inicioPorcentagemPista()[1] - pontoCentralContagemCheckpoint[1]));
		}
		iCkAnt = ckDep[0][2];
		r = new Random();
		float por = r.nextFloat();
		pontoCheckpoint[0] = ckDep[0][0][0] + por*(ckDep[0][1][0] - ckDep[0][0][0]);
		pontoCheckpoint[1] = ckDep[0][0][1] + por*(ckDep[0][1][1] - ckDep[0][0][1]);
		tempoFinalizacao = 0;
		colisaoEspermicida = false;
		flagContaVolta = false;
		contVoltas = 0;
	}

	public void mudaOrientacao(int o) {
		int quadrante = (int)o/4;
		int angulacao = o%4;
		int[] seq1 = {(3-angulacao)*10 + personagem*2,(3-angulacao)*10 + 1 + personagem*2};
		corpo.setFrameSequence(seq1);
		int seq2[] = new int[2],x,y;
		int transform;
		if(quadrante==0) {
			orientacao[0] = DIRECOES_QUADRANTE[angulacao][0];
			orientacao[1] = -DIRECOES_QUADRANTE[angulacao][1];
			transform = Sprite.TRANS_NONE;
		}
		else if(quadrante==1) {
			orientacao[0] = -DIRECOES_QUADRANTE[angulacao][1];
			orientacao[1] = -DIRECOES_QUADRANTE[angulacao][0];
			transform = Sprite.TRANS_ROT270;
		}
		else if(quadrante==2) {
			orientacao[0] = -DIRECOES_QUADRANTE[angulacao][0];
			orientacao[1] = DIRECOES_QUADRANTE[angulacao][1];
			transform = Sprite.TRANS_ROT180;
		}
		else {
			orientacao[0] = DIRECOES_QUADRANTE[angulacao][1];
			orientacao[1] = DIRECOES_QUADRANTE[angulacao][0];
			transform = Sprite.TRANS_ROT90;
		}
		corpo.setTransform(transform);
		cauda.setTransform(transform);
		if(angulacao==0) {
			if(personagem==3) {
				seq2[0] = 14;
				seq2[1] = 15;
			}
			else if(personagem==4) {
				seq2[0] = 22;
				seq2[1] = 23;
			}
			else {
				seq2[0] = 6;
				seq2[1] = 7;
			}
			x = 23 + corpo.getWidth()/2;
			y = 14;
		}
		else if(angulacao==1) {
			if(personagem==3) {
				seq2[0] = 12;
				seq2[1] = 13;
			}
			else if(personagem==4) {
				seq2[0] = 20;
				seq2[1] = 21;
			}
			else {
				seq2[0] = 4;
				seq2[1] = 5;
			}
			x = 23 + corpo.getWidth()/2 - 2;
			y = 10 - corpo.getHeight()/2 + 8;
		}
		else if(angulacao==2) {
			if(personagem==3) {
				seq2[0] = 10;
				seq2[1] = 11;
			}
			else if(personagem==4) {
				seq2[0] = 18;
				seq2[1] = 19;
			}
			else {
				seq2[0] = 2;
				seq2[1] = 3;
			}
			x = 21 + corpo.getWidth()/2 - 3;
			y = 5 - corpo.getHeight()/2 + 3;
		}
		else {
			if(personagem==3) {
				seq2[0] = 8;
				seq2[1] = 9;
			}
			else if(personagem==4) {
				seq2[0] = 16;
				seq2[1] = 17;
			}
			else {
				seq2[0] = 0;
				seq2[1] = 1;
			}
			x = 16 + corpo.getWidth()/2 - 6;
			y = 2 - corpo.getHeight()/2 + 3;
		}
		cauda.setFrameSequence(seq2);
		cauda.defineReferencePixel(x,y);
		cauda.setRefPixelPosition(corpo.getRefPixelX(),corpo.getRefPixelY());
		posicao_inicial[0] = corpo.getRefPixelX();
		posicao_inicial[1] = corpo.getRefPixelY();
		float modulo = (float)Math.sqrt(velocidade[0]*velocidade[0] + velocidade[1]*velocidade[1]);
		float[] v = {velocidade[0],velocidade[1]};
		if(modulo!=0) {
			v[0] = v[0]/modulo;
			v[1] = v[1]/modulo;
		}
		float[] or1 = {orientacao[0],orientacao[1]};
		//Se o vetor velocidade esta entre a orientacao anterior e a orientacao nova
		if(colisaoEspermicida||(((v[0]>=or1[0]-0.000001)&&(v[0]<=orientacao[0]+0.000001))||((v[0]<=or1[0]+0.000001)&&(v[0]>=orientacao[0]-0.000001)))&&(((v[1]>=or1[1]-0.000001)&&(v[1]<=orientacao[1]+0.000001))||((v[1]<=or1[1]+0.000001)&&(v[1]>=orientacao[1]-0.000001)))) {
			velocidade_inicial[0] = orientacao[0]*modulo;
			velocidade_inicial[1] = orientacao[1]*modulo;
		}
		else {
			velocidade_inicial[0] = velocidade[0];
			velocidade_inicial[1] = velocidade[1];
		}
		subrotinaAtualizaAceleracao(orientacao);
		tempo[0] = 0;
		tempo[1] = 0;
	}

	public void colisaoPista(Pista pista) {
		boolean flag = false;
		if(corpo.collidesWith(pista.retornaCamadaDeTiles(),false)) {
			if(corpo.collidesWith(pista.retornaCamadaDeTiles(),true))
				trataColisaoParede(pista);
			else if(colisaoPista)
				flag = true;
		}
		else if(colisaoPista)
			flag = true;
		if(flag) {
			int[] pos = {corpo.getRefPixelX(),corpo.getRefPixelY()};
			corpo.move((int)((corpo.getWidth()/2)*orientacao[0]),(int)((corpo.getHeight()/2)*orientacao[1]));
			if(!corpo.collidesWith(pista.retornaCamadaDeTiles(),true)) {
				posicao_inicial[0] = pos[0];
				posicao_inicial[1] = pos[1];
				velocidade_inicial[0] = velocidade[0];
				velocidade_inicial[1] = velocidade[1];
				tempo[0] = 0;
				tempo[1] = 0;
				subrotinaAtualizaAceleracao(orientacao);
				colisaoPista = false;
				System.gc();
			}
			corpo.setRefPixelPosition(pos[0],pos[1]);
		}
	}

	public void colisaoEpermicida(Inimigos in) {
		if(in!=null) {
			if(corpo.collidesWith(in.retornaEspermicida(), false) && in.retornaEspermicida().isVisible()){  //Verifica Colisão pelo método do retângulo
				if(corpo.collidesWith(in.retornaEspermicida(), true)) //Verifica Colisão por Pixel
					trataColisaoEspermicida(); //Tratar a Colisão do Espermicida
				else if(colisaoEspermicida) //Senão
					trataDescolisaoEspermicida();
			}
			else if(colisaoEspermicida) //Senão
				trataDescolisaoEspermicida();
		}
	}

	public float retornaDistanciaMaximaPontoCentral() {
		return distanciaMaximaPontoCentral;
	}

	public float retornaPorcentagemPista(Pista pista) {
		float dist = (float)Math.sqrt((corpo.getRefPixelX() - pontoCentralContagemCheckpoint[0])*(corpo.getRefPixelX() - pontoCentralContagemCheckpoint[0]) + (corpo.getRefPixelY() - pontoCentralContagemCheckpoint[1])*(corpo.getRefPixelY() - pontoCentralContagemCheckpoint[1]));
		int par = contCheckpoint;
		if(pista.fazVoltas()) {
			par -= 1;
			if(par<0)
				par = pista.retornaPorcentagemPista().length - 1;
		}
		return (float)(par)/pista.retornaPorcentagemPista().length + (1f/(pista.retornaPorcentagemPista().length))*((distanciaMaximaPontoCentral-dist)/distanciaMaximaPontoCentral);
	}

	public float[] retornaPontoCheckpoint() {
		return pontoCheckpoint;
	}

	public float[] retornaOrientacao() {
		return orientacao;
	}

	public Sprite retornaSprite() {
		return corpo;
	}

	public long retornaTempo() {
		return tempoFinalizacao;
	}

	public long retornaTempo(long t,Pista pista) {
		float por = 1f - retornaPorcentagemPista(pista);
		return t + (long)(pista.retornaMediaTempo()*1000*por);
	}

	public short[][] retornaIndiceCheckpoint() {
		return new short[][] {iCkDep,iCkAnt};
	}

	public void adicionaCamada(GerenciadorCamadas lm) {
		lm.adiciona(corpo);
		lm.adiciona(cauda);
	}

	public void moveCompetidor(int deltaTempo,long tempoAtual,GerenciadorCamadas gc,Pista pista) {
		tempo[0] += deltaTempo;
		tempo[1] += deltaTempo;
		float velocidade_maxima = VELOCIDADE_MAXIMA_SEM_NITRO;
		if(nitro) {
			velocidade_maxima = VELOCIDADE_COM_NITRO;
			tempoNitro += deltaTempo;
			if(tempoNitro>=2000) {
				tempo[0] += deltaTempo - (tempoNitro - 2000);
				tempo[1] += deltaTempo - (tempoNitro - 2000);
				posicao_inicial[0] += (int)((2000*velocidade_inicial[0]*tempo[0] + aceleracao[0]*tempo[0]*tempo[0])/2000000);
				posicao_inicial[1] += (int)((2000*velocidade_inicial[1]*tempo[1] + aceleracao[1]*tempo[1]*tempo[1])/2000000);
				velocidade[0] = (1000*velocidade_inicial[0] + aceleracao[0]*tempo[0])/1000;
				float vMax = orientacao[0]*VELOCIDADE_MAXIMA_SEM_NITRO;
				/**************************************Verificar este if****************************************************/
				if(Math.abs(velocidade[0])>Math.abs(vMax)) {
					velocidade_inicial[0] = vMax;
					aceleracao[0] = 0;
				}
				else{
					velocidade_inicial[0] = velocidade[0];
					aceleracao[0] = orientacao[0]*ACELERACAO_SEM_NITRO;
				}
				velocidade[1] = (1000*velocidade_inicial[1] + aceleracao[1]*tempo[1])/1000;
				vMax = orientacao[1]*VELOCIDADE_MAXIMA_SEM_NITRO;
				if(Math.abs(velocidade[1])>Math.abs(vMax)) {
					velocidade_inicial[1] = vMax;
					aceleracao[1] = 0;
				}
				else {
					velocidade_inicial[1] = velocidade[1];
					aceleracao[1] = orientacao[1]*ACELERACAO_SEM_NITRO;
				}
				tempo[0] = tempoNitro - 2000;
				tempo[1] = tempo[0];
				nitro = false;
			}

		}
		else if(colisaoEspermicida) {
			velocidade_maxima = VELOCIDADE_ESPERMICIDA;
		}
		velocidade[0] = (1000*velocidade_inicial[0] + aceleracao[0]*tempo[0])/1000;
		float vMax = orientacao[0]*velocidade_maxima;
		if((vMax<velocidade[0]&&vMax>=0&&velocidade[0]>0)||(vMax>velocidade[0]&&vMax<=0&&velocidade[0]<0)) {
			if((aceleracao[0]>0&&velocidade[0]>=0)||(aceleracao[0]<0&&velocidade[0]<=0)) {
				posicao_inicial[0] += (vMax*vMax - velocidade_inicial[0]*velocidade_inicial[0])/(2*aceleracao[0]);
				tempo[0] -= (1000*(vMax - velocidade_inicial[0]))/aceleracao[0];
				velocidade_inicial[0] = vMax;
				velocidade[0] = vMax;
				aceleracao[0] = 0;
			}
		}
		else if((vMax>velocidade[0]&&vMax>=0&&velocidade[0]>0)||(vMax<velocidade[0]&&vMax<=0&&velocidade[0]<0)) {
			if((aceleracao[0]<0&&velocidade[0]>=0)||(aceleracao[0]>0&&velocidade[0]<=0)) {
				posicao_inicial[0] += (vMax*vMax - velocidade_inicial[0]*velocidade_inicial[0])/(2*aceleracao[0]);
				tempo[0] -= (1000*(vMax - velocidade_inicial[0]))/aceleracao[0];
				velocidade_inicial[0] = vMax;
				velocidade[0] = vMax;
				aceleracao[0] = 0;
			}
		}
		velocidade[1] = (1000*velocidade_inicial[1] + aceleracao[1]*tempo[1])/1000;
		vMax = orientacao[1]*velocidade_maxima;
		if((vMax<velocidade[1]&&vMax>=0&&velocidade[1]>0)||(vMax>velocidade[1]&&vMax<=0&&velocidade[1]<0)) {
			if((aceleracao[1]>0&&velocidade[1]>=0)||(aceleracao[1]<0&&velocidade[1]<=0)) {
				posicao_inicial[1] += (vMax*vMax - velocidade_inicial[1]*velocidade_inicial[1])/(2*aceleracao[1]);
				tempo[1] -= (1000*(vMax - velocidade_inicial[1]))/aceleracao[1];
				velocidade_inicial[1] = vMax;
				velocidade[1] = vMax;
				aceleracao[1] = 0;
			}
		}
		else if((vMax>velocidade[1]&&vMax>=0&&velocidade[1]>0)||(vMax<velocidade[1]&&vMax<=0&&velocidade[1]<0)) {
			if((aceleracao[1]<0&&velocidade[1]>=0)||(aceleracao[1]>0&&velocidade[1]<=0)) {
				posicao_inicial[1] += (vMax*vMax - velocidade_inicial[1]*velocidade_inicial[1])/(2*aceleracao[1]);
				tempo[1] -= (1000*(vMax - velocidade_inicial[1]))/aceleracao[1];
				velocidade_inicial[1] = vMax;
				velocidade[1] = vMax;
				aceleracao[1] = 0;
			}
		}
		float deltaSX = (2000*velocidade_inicial[0]*tempo[0] + aceleracao[0]*tempo[0]*tempo[0])/2000000; 	// dS = V0T + AT²/2
		float deltaSY = (2000*velocidade_inicial[1]*tempo[1] + aceleracao[1]*tempo[1]*tempo[1])/2000000; 	// dS = V0T + AT²/2
		corpo.setRefPixelPosition((int)(posicao_inicial[0]+deltaSX),(int)(posicao_inicial[1]+deltaSY));
		cauda.setRefPixelPosition((int)(posicao_inicial[0]+deltaSX),(int)(posicao_inicial[1]+deltaSY));
		float modulo = (float)Math.sqrt(velocidade[0]*velocidade[0] + velocidade[1]*velocidade[1]);
		float cosA = (velocidade[0]*orientacao[0] + velocidade[1]*orientacao[1])/modulo;
		if(cosA<0.999999&&cosA>=DIRECOES_QUADRANTE[1][0]) {
			posicao_inicial[0] = corpo.getRefPixelX();
			posicao_inicial[1] = corpo.getRefPixelY();
			velocidade_inicial[0] = orientacao[0]*modulo;
			velocidade_inicial[1] = orientacao[1]*modulo;
			velocidade[0] = velocidade_inicial[0];
			velocidade[1] = velocidade_inicial[1];
			float vMax2;
			if(nitro) {
				aceleracao[0] = orientacao[0]*ACELERACAO_COM_NITRO;
				aceleracao[1] = orientacao[1]*ACELERACAO_COM_NITRO;
				vMax = orientacao[0]*VELOCIDADE_COM_NITRO;
				vMax2 = orientacao[1]*VELOCIDADE_COM_NITRO;
			}
			else if(colisaoEspermicida) {
				aceleracao[0] = orientacao[0]*ACELERACAO_ESPERMICIDA;
				aceleracao[1] = orientacao[1]*ACELERACAO_ESPERMICIDA;
				vMax = orientacao[0]*VELOCIDADE_ESPERMICIDA;
				vMax2 = orientacao[1]*VELOCIDADE_ESPERMICIDA;
			}
			else {
				aceleracao[0] = orientacao[0]*ACELERACAO_SEM_NITRO;
				aceleracao[1] = orientacao[1]*ACELERACAO_SEM_NITRO;
				vMax = orientacao[0]*VELOCIDADE_MAXIMA_SEM_NITRO;
				vMax2 = orientacao[1]*VELOCIDADE_MAXIMA_SEM_NITRO;
			}
			if((vMax<velocidade_inicial[0]&&vMax>=0&&velocidade_inicial[0]>0)||(vMax>velocidade_inicial[0]&&vMax<=0&&velocidade_inicial[0]<0)) {
				aceleracao[0] = Math.abs(aceleracao[1]);
				if(aceleracao[0]==0) {
					if(nitro)
						aceleracao[0] = DIRECOES_QUADRANTE[3][0]*ACELERACAO_COM_NITRO;
					else if(colisaoEspermicida)
						aceleracao[0] = DIRECOES_QUADRANTE[3][0]*ACELERACAO_ESPERMICIDA;
					else
						aceleracao[0] = DIRECOES_QUADRANTE[3][0]*ACELERACAO_SEM_NITRO;
				}
				if(velocidade_inicial[0]>0)
					aceleracao[0] = -aceleracao[0];
			}
			if((vMax2<velocidade_inicial[1]&&vMax2>=0&&velocidade_inicial[1]>0)||(vMax2>velocidade_inicial[1]&&vMax2<=0&&velocidade_inicial[1]<0)) {
				aceleracao[1] = Math.abs(aceleracao[0]);
				if(aceleracao[1]==0) {
					if(nitro)
						aceleracao[1] = DIRECOES_QUADRANTE[3][0]*ACELERACAO_COM_NITRO;
					else if(colisaoEspermicida)
						aceleracao[1] = DIRECOES_QUADRANTE[3][0]*ACELERACAO_ESPERMICIDA;
					else
						aceleracao[1] = DIRECOES_QUADRANTE[3][0]*ACELERACAO_SEM_NITRO;
				}
				if(velocidade_inicial[1]>0)
					aceleracao[1] = -aceleracao[1];
			}
			tempo[0] = 0;
			tempo[1] = 0;
		}
		float ds = (float)Math.sqrt(deltaSX*deltaSX + deltaSY*deltaSY);
		if(deltaSMudFrmCorpo > ds%10)
			corpo.nextFrame();
		deltaSMudFrmCorpo = ds%10;
		deltaTMudFrmCalda += deltaTempo;
		if(deltaTMudFrmCalda >= 80) {
			deltaTMudFrmCalda -= 80;
			cauda.nextFrame();
		}
		atualizaCheckpoint(tempoAtual,gc,pista);
	}

	public void trataColisaoDiafragma(Inimigos in) {
		float r = Math.max(in.retornaDiafragma().getWidth(),in.retornaDiafragma().getHeight())/2;
		float c1 = in.retornaDiafragma().getRefPixelX();
		float c2 = in.retornaDiafragma().getRefPixelY();
		float x1 = corpo.getRefPixelX();
		float y1 = corpo.getRefPixelY();
		if(Math.sqrt((x1 - c1)*(x1 - c1) + (y1 - c2)*(y1 - c2))<=r) {
			float v1 = velocidade[0];
			float v2 = velocidade[1];
			float aux = x1 - c1;
			float a = v1*v1 + v2*v2;
			float b = v1*v2*aux - y1*v1*v1 - v2*v2*c2;
			float c = v1*v1*y1*y1 - 2*v1*v2*y1*aux - v2*v2*(r*r - aux*aux - c2*c2);
			float raiz_delta = b*b - a*c;
			//Caso existe interseccao entre a reta e o circulo
			if(raiz_delta>=0) {
				float[] vRepDiafragma = new float[2];
				if(a!=0) {
					float[] t = new float[2];
					float[][] pontos = new float[2][2];
					raiz_delta = (float)Math.sqrt(raiz_delta);
					/*******************************Rever fómula 2 grau aqui****************************************/
					pontos[0][1] = -(b + raiz_delta)/a;
					pontos[1][1] = -(b - raiz_delta)/a;
					if(v2!=0) {
						pontos[0][0] = (v2*x1 + v1*(pontos[0][1] - y1))/v2;
						pontos[1][0] = (v2*x1 + v1*(pontos[1][1] - y1))/v2;
					}
					else {
						aux = pontos[0][1] - c2;
						pontos[0][0] = (float)(c1 + Math.sqrt(r*r - aux*aux));
						pontos[1][0] = (float)(c1 - Math.sqrt(r*r - aux*aux));
					}
					float dist1 = (pontos[0][0] - posicao_inicial[0])*(pontos[0][0] - posicao_inicial[0]) + (pontos[0][1] - posicao_inicial[1])*(pontos[0][1] - posicao_inicial[1]);
					float dist2 = (pontos[1][0] - posicao_inicial[0])*(pontos[1][0] - posicao_inicial[0]) + (pontos[1][1] - posicao_inicial[1])*(pontos[1][1] - posicao_inicial[1]);
					int i = 0;
					if(dist1>dist2)
						i = 1;
					float ds,vi;
					a = aceleracao[0];
					vi = velocidade_inicial[0];
					ds = pontos[i][0] - posicao_inicial[0];
					if(a==0) {
						t[0] = -1;
						if(vi!=0)
							t[0] = ds/vi;
					}
					else {
						raiz_delta = vi*vi + 2*a*ds;
						if(raiz_delta>=0) {
							t[0] = (float)((-vi + Math.sqrt(raiz_delta))/a);
							float t2 = (float)((-vi - Math.sqrt(raiz_delta))/a);
							if(Math.abs(Math.abs(t[0])*1000-tempo[0])>Math.abs(Math.abs(t2)*1000-tempo[0]))
								t[0] = t2;
						}
						else
							t[0] = -1;
					}
					if(t[0]<0||t[0]*1000>tempo[0]) {
						t[0] = 0;
						tempo[0] = 0;
					}
					a = aceleracao[1];
					vi = velocidade_inicial[1];
					ds = pontos[i][1] - posicao_inicial[1];
					if(a==0) {
						t[1] = -1;
						if(vi!=0)
							t[1] = ds/vi;
					}
					else {
						raiz_delta = vi*vi + 2*a*ds;
						if(raiz_delta>=0) {
							t[1] = (float)((-vi + Math.sqrt(raiz_delta))/a);
							float t2 = (float)((-vi - Math.sqrt(raiz_delta))/a);
							if(Math.abs(Math.abs(t[1])*1000-tempo[1])>Math.abs(Math.abs(t2)*1000-tempo[1]))
								t[1] = t2;
						}
						else
							t[1] = -1;
					}
					if(t[1]<0||t[1]*1000>tempo[1]) {
						t[1] = 0;
						tempo[1] = 0;
					}

					vRepDiafragma[0] = (pontos[i][0] - c1)/r;
					vRepDiafragma[1] = (pontos[i][1] - c2)/r;
					posicao_inicial[0] = (int)pontos[i][0];
					posicao_inicial[1] = (int)pontos[i][1];
					velocidade[0] = velocidade_inicial[0] + aceleracao[0]*t[0];
					if(Math.abs(vRepDiafragma[0])>=Math.abs(vRepDiafragma[1])&&((velocidade[0]<in.retornaVelocidadeDiafragma()&&velocidade[0]>=0&&in.retornaVelocidadeDiafragma()>0)||(velocidade[0]>in.retornaVelocidadeDiafragma()&&velocidade[0]<=0&&in.retornaVelocidadeDiafragma()<0))) {
						if(corpo.getRefPixelX()<in.retornaDiafragma().getRefPixelX())
							velocidade_inicial[0] = -Math.abs(in.retornaVelocidadeDiafragma());
						else if(corpo.getRefPixelX()>in.retornaDiafragma().getRefPixelX())
							velocidade_inicial[0] = Math.abs(in.retornaVelocidadeDiafragma());
						else
							velocidade_inicial[0] = in.retornaVelocidadeDiafragma();
						t[0] = 0;
						tempo[0] = 0;
					}
					velocidade[1] = velocidade_inicial[1] + aceleracao[1]*t[1];
					float modulo = (float)Math.sqrt(velocidade[0]*velocidade[0] + velocidade[1]*velocidade[1]);
					velocidade_inicial[0] = modulo*vRepDiafragma[0];
					velocidade_inicial[1] = modulo*vRepDiafragma[1];
					tempo[0] -= t[0]*1000;
					tempo[1] -= t[1]*1000;
					subrotinaAtualizaAceleracao(orientacao);
				}
				else {
					//Nao tem reta
					float velocidadeDiafragma = in.retornaVelocidadeDiafragma();
					vRepDiafragma[0] = corpo.getRefPixelX() - in.retornaDiafragma().getRefPixelX();
					vRepDiafragma[1] = corpo.getRefPixelY() - in.retornaDiafragma().getRefPixelY();
					float modulo = (float)Math.sqrt(vRepDiafragma[0]*vRepDiafragma[0] + vRepDiafragma[1]*vRepDiafragma[1]);
					if(modulo!=0) {
						vRepDiafragma[0] /= modulo;
						vRepDiafragma[1] /= modulo;
					}
					else if(velocidadeDiafragma<0)
						vRepDiafragma[0] = -1;
					else
						vRepDiafragma[0] = 1;
					posicao_inicial[0] = (int)(c1 + vRepDiafragma[0]*r);
					posicao_inicial[1] = (int)(c2 + vRepDiafragma[1]*r);
					if(corpo.getRefPixelX()<in.retornaDiafragma().getRefPixelX())
						velocidade_inicial[0] = -Math.abs(velocidadeDiafragma);
					else if(corpo.getRefPixelX()>in.retornaDiafragma().getRefPixelX())
						velocidade_inicial[0] = Math.abs(velocidadeDiafragma);
					else
						velocidade_inicial[0] = velocidadeDiafragma;
					velocidade_inicial[1] = vRepDiafragma[1]*Math.abs(velocidadeDiafragma);
					velocidade[0] = velocidade_inicial[0];
					velocidade[1] = velocidade_inicial[1];
					tempo[0] = 0;
					tempo[1] = 0;
					subrotinaAtualizaAceleracao(orientacao);
				}
				System.gc();
			}
		}
	}

	public void trataColisaoExterminador(Inimigos in) {
		/* Dá para otimizar */		

		float r = Math.max(in.retornaExterminador().getHeight(), in.retornaExterminador().getWidth())/2;
		float ix = in.retornaExterminador().getRefPixelX();
		float iy = in.retornaExterminador().getRefPixelY();
		float cx = corpo.getRefPixelX();
		float cy = corpo.getRefPixelY();
		float dist =(float)Math.sqrt((ix-cx)*(ix-cx)+(iy-cy)*(iy-cy));
		int[] v = {0,0};
		int[] v1 = {0,0};
		if(r>=dist && dist!=0) {
			v[0] = (int)(((cx-ix)/dist)*r);
			v[1] = (int)(((cy-iy)/dist)*r);
			v1[0] = (int)(v[0]-(cx-ix)); //Tirar o vetor direcao entre a posicao do inimigo 
			v1[1] = (int)(v[1]-(cy-iy)); //e do competidor 
			//Transformar em unitário (modulo é a distancia)
			//E mutiplica pelo raio 
			//E diminui pela distancia entre competidor e inimigo
		} else if(dist==0) {
			dist = (float)Math.sqrt((ix-posicao_inicial[0])*(ix-posicao_inicial[0])+(iy-posicao_inicial[1])*(iy-posicao_inicial[1]));
			v1[0] = (int)(((posicao_inicial[0]-ix)/dist)*r);
			v1[1] = (int)(((posicao_inicial[1]-iy)/dist)*r);
			v[0] = v1[0];
			v[1] = v1[1];
		}
		posicao_inicial[0] = (int)cx+v1[0];
		posicao_inicial[1] = (int)cy+v1[1];

		if(v[0]>0)
			velocidade_inicial[0]=in.retornaVelocidadeExterminador();
		if((v[1]>0 && velocidade_inicial[1]<0) || ((v[1]<0 && velocidade_inicial[1]>0))) {
			float Vy = (r-Math.abs(v[1]))/r;
			float aux = in.retornaVelocidadeExterminador()*Vy;
			if((aux<0 && velocidade_inicial[1]>0)||(aux>0 && velocidade_inicial[1]<0))
				aux =- aux;
			if((aux>0 && velocidade_inicial[1]>0)||(aux<0 && velocidade_inicial[1]<0))
				velocidade_inicial[1] = aux;
		}
		velocidade[0] = velocidade_inicial[0];
		velocidade[1] = velocidade_inicial[1];
		tempo[0] = 0;
		tempo[1] = 0;
		subrotinaAtualizaAceleracao(orientacao);
	}

	public void trataColisaoCompetidores(Competidor concorrente) {

		float[] vc = {corpo.getRefPixelX() - concorrente.corpo.getRefPixelX(),corpo.getRefPixelY() - concorrente.corpo.getRefPixelY()};
		float[] pc = {(concorrente.corpo.getRefPixelX() + corpo.getRefPixelX())/2,(concorrente.corpo.getRefPixelY() + corpo.getRefPixelY())/2};
		float r = Math.max(corpo.getWidth(),corpo.getHeight())/2;
		float moduloVc = (float)Math.sqrt(vc[0]*vc[0] + vc[1]*vc[1]);
		vc[0] /= moduloVc;
		vc[1] /= moduloVc;
		posicao_inicial[0] = (int)(pc[0] + vc[0]*r);
		posicao_inicial[1] = (int)(pc[1] + vc[1]*r);
		concorrente.posicao_inicial[0] = (int)(pc[0] - vc[0]*r);
		concorrente.posicao_inicial[1] = (int)(pc[1] - vc[1]*r);

		float[] n = {concorrente.posicao_inicial[0]-posicao_inicial[0],concorrente.posicao_inicial[1]-posicao_inicial[1]};
		float[] Vab = {velocidade[0]-concorrente.velocidade[0],velocidade[1]-concorrente.velocidade[1]};		
		float j = (-dotProduct(Vab,n)/(dotProduct(n,n)));

		velocidade[0] = velocidade[0]+j*n[0];
		velocidade[1] = velocidade[1]+j*n[1];

		concorrente.velocidade[0] = concorrente.velocidade[0]-j*n[0];
		concorrente.velocidade[1] = concorrente.velocidade[1]-j*n[1];

		tempo[0] = 0;
		tempo[1] = 0;
		concorrente.tempo[0] = 0;
		concorrente.tempo[1] = 0;
		velocidade_inicial[0] = velocidade[0];
		velocidade_inicial[1] = velocidade[1];
		concorrente.velocidade_inicial[0] = concorrente.velocidade[0];
		concorrente.velocidade_inicial[1] = concorrente.velocidade[1];
		subrotinaAtualizaAceleracao(orientacao);
		concorrente.subrotinaAtualizaAceleracao(concorrente.orientacao);
	}

	private float dotProduct(float[] v1, float[] v2){
		return (v1[0]*v2[0]+v1[1]*v2[1]);
	}

	public boolean contraMao() {
		float[] vetorDirecao = {pontoCentralContagemCheckpoint[0] - corpo.getRefPixelX(),pontoCentralContagemCheckpoint[1] - corpo.getRefPixelY()};
		float modulo = (float)Math.sqrt(vetorDirecao[0]*vetorDirecao[0] + vetorDirecao[1]*vetorDirecao[1]);
		if(modulo>0) {
			vetorDirecao[0] /= modulo;
			vetorDirecao[1] /= modulo;
		}
		float cos = vetorDirecao[0]*orientacao[0] + vetorDirecao[1]*orientacao[1];
		if(cos<-0.5)
			return true;
		return false;
	}

	private void trataColisaoParede(Pista pista) {
		float[] or = {velocidade[0],velocidade[1]};
		float modulo = (float)Math.sqrt(velocidade[0]*velocidade[0] + velocidade[1]*velocidade[1]);
		//A velocidade pode ser zero
		if(modulo!=0) {
			int[] p = {(int)(corpo.getRefPixelX()/pista.retornaCamadaDeTiles().getCellWidth()),(int)(corpo.getRefPixelY()/pista.retornaCamadaDeTiles().getCellHeight())};
			int celula;
			if(p[0]<pista.retornaCamadaDeTiles().getColumns()&&p[0]>=0&&p[1]<pista.retornaCamadaDeTiles().getRows()&&p[1]>=0)
				celula = pista.retornaCamadaDeTiles().getCell(p[0],p[1]);
			else
				celula = 1;
			float[] vetorDirecao = retornaVetorDirecao(celula);
			if(vetorDirecao!=null) {
				float[] vetorRepulsao = retornaVetorRepulsao(celula);
				or[0] /= modulo;
				or[1] /= modulo;
				float cos = or[0]*vetorRepulsao[0] + or[1]*vetorRepulsao[1];
				if(cos<0) {
					cos = or[0]*vetorDirecao[0] + or[1]*vetorDirecao[1];
					if(cos<0) {
						vetorDirecao[0] = -vetorDirecao[0];
						vetorDirecao[1] = -vetorDirecao[1];
						cos = or[0]*vetorDirecao[0] + or[1]*vetorDirecao[1];
					}
					float modVelocidade = modulo*cos;
					posicao_inicial[0] = corpo.getRefPixelX();
					posicao_inicial[1] = corpo.getRefPixelY();
					velocidade_inicial[0] = vetorDirecao[0]*modVelocidade;
					velocidade_inicial[1] = vetorDirecao[1]*modVelocidade;
					velocidade[0] = velocidade_inicial[0];
					velocidade[1] = velocidade_inicial[1];
					tempo[0] = 0;
					tempo[1] = 0;
					colisaoPista = true;
					cos = orientacao[0]*vetorDirecao[0] + orientacao[1]*vetorDirecao[1];
					if(cos<0) {
						vetorDirecao[0] = -vetorDirecao[0];
						vetorDirecao[1] = -vetorDirecao[1];
					}
					subrotinaAtualizaAceleracao(vetorDirecao);
				}
				int i;
				int[] pAnt = {corpo.getRefPixelX(),corpo.getRefPixelY()};
				for(i=0;corpo.collidesWith(pista.retornaCamadaDeTiles(),true);i+=1)
					corpo.setRefPixelPosition((int)(pAnt[0] + i*vetorRepulsao[0]),(int)(pAnt[1] + i*vetorRepulsao[1]));
				posicao_inicial[0] += i*vetorRepulsao[0];
				posicao_inicial[1] += i*vetorRepulsao[1];
			}
			else {
				//Unificacao dos tempos
				if(tempo[0]<tempo[1]) {
					int dif = tempo[1] - tempo[0];
					posicao_inicial[1] += (2000*velocidade_inicial[1]*dif + aceleracao[1]*dif*dif)/2000000;
					velocidade_inicial[1] = (1000*velocidade_inicial[1] + aceleracao[1]*dif)/1000;
					velocidade[1] = velocidade_inicial[1];
					tempo[1] = tempo[0];
				}
				else if(tempo[0]>tempo[1]) {
					int dif = tempo[0] - tempo[1];
					posicao_inicial[0] += (2000*velocidade_inicial[0]*dif + aceleracao[0]*dif*dif)/2000000;
					velocidade_inicial[0] = (1000*velocidade_inicial[0] + aceleracao[0]*dif)/1000;
					velocidade[0] = velocidade_inicial[0];
					tempo[0] = tempo[1];
				}

				//Verificação de colisão na posicao inicial
				float[] vetorRepulsao = null;
				{
					int posAnt[] = {corpo.getRefPixelX(),corpo.getRefPixelY()};
					corpo.setRefPixelPosition(posicao_inicial[0],posicao_inicial[1]);
					if(corpo.collidesWith(pista.retornaCamadaDeTiles(),true)) {
						int dt = 0;
						int cel;
						p[0] = posicao_inicial[0];
						p[1] = posicao_inicial[1];
						if(p[0]/pista.retornaCamadaDeTiles().getCellWidth()<pista.retornaCamadaDeTiles().getColumns()&&p[0]>=0&&p[1]/pista.retornaCamadaDeTiles().getCellHeight()<pista.retornaCamadaDeTiles().getRows()&&p[1]>=0)
							cel = pista.retornaCamadaDeTiles().getCell(p[0]/pista.retornaCamadaDeTiles().getCellWidth(),p[1]/pista.retornaCamadaDeTiles().getCellHeight());
						else
							cel = 1;
						while(cel==1) {
							dt -= 25;
							p[0] = (int)(posicao_inicial[0] + (2000*velocidade_inicial[0]*dt + aceleracao[0]*dt*dt)/2000000);
							p[1] = (int)(posicao_inicial[1] + (2000*velocidade_inicial[1]*dt + aceleracao[1]*dt*dt)/2000000);
							corpo.setRefPixelPosition(p[0],p[1]);
							if(p[0]/pista.retornaCamadaDeTiles().getCellWidth()<pista.retornaCamadaDeTiles().getColumns()&&p[0]>=0&&p[1]/pista.retornaCamadaDeTiles().getCellHeight()<pista.retornaCamadaDeTiles().getRows()&&p[1]>=0)
								cel = pista.retornaCamadaDeTiles().getCell(p[0]/pista.retornaCamadaDeTiles().getCellWidth(),p[1]/pista.retornaCamadaDeTiles().getCellHeight());
							else
								cel = 1;
						}
						posicao_inicial[0] = corpo.getRefPixelX();
						posicao_inicial[1] = corpo.getRefPixelY();
						velocidade[0] = (1000*velocidade_inicial[0] + aceleracao[0]*dt)/1000;
						velocidade[1] = (1000*velocidade_inicial[1] + aceleracao[1]*dt)/1000;
						velocidade_inicial[0] = velocidade[0];
						velocidade_inicial[1] = velocidade[1];
						tempo[0] -= dt;
						tempo[1] -= dt;
						vetorRepulsao = retornaVetorRepulsao(cel);
						int i;
						int[] pAnt = {corpo.getRefPixelX(),corpo.getRefPixelY()};
						for(i=0;corpo.collidesWith(pista.retornaCamadaDeTiles(),true);i+=1)
							corpo.setRefPixelPosition((int)(pAnt[0] + i*vetorRepulsao[0]),(int)(pAnt[1] + i*vetorRepulsao[1]));
						posicao_inicial[0] = corpo.getRefPixelX();
						posicao_inicial[1] = corpo.getRefPixelY();
					}
					corpo.setRefPixelPosition(posAnt[0],posAnt[1]);
				}
				//Derivação das novas posicoes iniciais
				while(corpo.collidesWith(pista.retornaCamadaDeTiles(),true)||corpo.getRefPixelX()<0||corpo.getRefPixelX()>pista.retornaCamadaDeTiles().getWidth()||corpo.getRefPixelY()<0||corpo.getRefPixelY()>pista.retornaCamadaDeTiles().getHeight()) {
					float deltaT = 0.0f;
					float cos = 0.0f;
					corpo.setRefPixelPosition(posicao_inicial[0],posicao_inicial[1]);
					while(!corpo.collidesWith(pista.retornaCamadaDeTiles(),true)&&1000*(deltaT + 0.05f)<=tempo[0]) {
						deltaT += 0.05f;
						p[0] = (int)(0.5f*(2*(posicao_inicial[0] + velocidade_inicial[0]*deltaT) + aceleracao[0]*deltaT*deltaT));
						p[1] = (int)(0.5f*(2*(posicao_inicial[1] + velocidade_inicial[1]*deltaT) + aceleracao[1]*deltaT*deltaT));
						if(p[0]/pista.retornaCamadaDeTiles().getCellWidth()<pista.retornaCamadaDeTiles().getColumns()&&p[0]>=0&&p[1]/pista.retornaCamadaDeTiles().getCellHeight()<pista.retornaCamadaDeTiles().getRows()&&p[1]>=0)
							celula = pista.retornaCamadaDeTiles().getCell(p[0]/pista.retornaCamadaDeTiles().getCellWidth(),p[1]/pista.retornaCamadaDeTiles().getCellHeight());
						else
							celula = 1;
						vetorRepulsao = retornaVetorRepulsao(celula);
						corpo.setRefPixelPosition(p[0],p[1]);
						if(vetorRepulsao!=null) {
							velocidade[0] = velocidade_inicial[0] + aceleracao[0]*deltaT;
							velocidade[1] = velocidade_inicial[1] + aceleracao[1]*deltaT;
							or[0] = velocidade[0];
							or[1] = velocidade[1];
							modulo = (float)Math.sqrt(velocidade[0]*velocidade[0] + velocidade[1]*velocidade[1]);
							if(modulo!=0) {
								or[0] /= modulo;
								or[1] /= modulo;
								cos = or[0]*vetorRepulsao[0] + or[1]*vetorRepulsao[1];
								if(cos>-0.02&&cos<0.02&&corpo.collidesWith(pista.retornaCamadaDeTiles(),true)) {
									int i;
									int[] pAnt = {corpo.getRefPixelX(),corpo.getRefPixelY()};
									for(i=0;corpo.collidesWith(pista.retornaCamadaDeTiles(),true);i+=1)
										corpo.setRefPixelPosition((int)(pAnt[0] + i*vetorRepulsao[0]),(int)(pAnt[1] + i*vetorRepulsao[1]));
									posicao_inicial[0] += i*vetorRepulsao[0];
									posicao_inicial[1] += i*vetorRepulsao[1];
								}
							}
						}
					}
					if(cos<0) {
						vetorDirecao = retornaVetorDirecao(celula);
						cos = or[0]*vetorDirecao[0] + or[1]*vetorDirecao[1];
						if(cos<0) {
							vetorDirecao[0] = -vetorDirecao[0];
							vetorDirecao[1] = -vetorDirecao[1];
							cos = or[0]*vetorDirecao[0] + or[1]*vetorDirecao[1];
						}
						float modVelocidade = modulo*cos;
						posicao_inicial[0] = corpo.getRefPixelX();
						posicao_inicial[1] = corpo.getRefPixelY();
						velocidade_inicial[0] = vetorDirecao[0]*modVelocidade;
						velocidade_inicial[1] = vetorDirecao[1]*modVelocidade;
						velocidade[0] = velocidade_inicial[0];
						velocidade[1] = velocidade_inicial[1];
						tempo[0] -= deltaT*1000;
						tempo[1] -= deltaT*1000;
						colisaoPista = true;
						cos = orientacao[0]*vetorDirecao[0] + orientacao[1]*vetorDirecao[1];
						if(cos<0) {
							vetorDirecao[0] = -vetorDirecao[0];
							vetorDirecao[1] = -vetorDirecao[1];
						}
						subrotinaAtualizaAceleracao(vetorDirecao);
					}

					int i;
					int[] pAnt = {corpo.getRefPixelX(),corpo.getRefPixelY()};
					for(i=0;corpo.collidesWith(pista.retornaCamadaDeTiles(),true);i+=1)
						corpo.setRefPixelPosition((int)(pAnt[0] + i*vetorRepulsao[0]),(int)(pAnt[1] + i*vetorRepulsao[1]));
					posicao_inicial[0] += i*vetorRepulsao[0];
					posicao_inicial[1] += i*vetorRepulsao[1];
					corpo.setRefPixelPosition((int)(posicao_inicial[0] + (2000*velocidade_inicial[0]*tempo[0] + aceleracao[0]*tempo[0]*tempo[0])/2000000),(int)(posicao_inicial[1] + (2000*velocidade_inicial[1]*tempo[1] + aceleracao[1]*tempo[1]*tempo[1])/2000000));
				}
			}
			cauda.setRefPixelPosition(corpo.getRefPixelX(),corpo.getRefPixelY());
		}
	}

	private float[] retornaVetorRepulsao(int cel) {
		float ret[] = null;
		if(cel==2||cel==5||cel==6||cel==9) {
			ret = new float[2];
			if(cel==2||cel==6) {
				ret[0] = -SIN_45;
				ret[1] = -SIN_45;
			}
			else {
				ret[0] = SIN_45;
				ret[1] = SIN_45;
			}
		}
		else if(cel==3||cel==4||cel==7||cel==8) {
			ret = new float[2];
			if(cel==3||cel==7) {
				ret[0] = SIN_45;
				ret[1] = -SIN_45;
			}
			else {
				ret[0] = -SIN_45;
				ret[1] = SIN_45;
			}
		}
		else if(cel==10||cel==11) {
			ret = new float[2];
			if(cel==10)
				ret[0] = -1;
			else
				ret[0] = 1;
			ret[1] = 0;
		}
		else if(cel==12||cel==13) {
			ret = new float[2];
			ret[0] = 0;
			if(cel==12)
				ret[1] = -1;
			else
				ret[1] = 1;
		}
		return ret;
	}

	private float[] retornaVetorDirecao(int cel) {
		float ret[] = null;
		if(cel==2||cel==5||cel==6||cel==9) {
			ret = new float[2];
			ret[0] = SIN_45;
			ret[1] = -SIN_45;
		}
		else if(cel==3||cel==4||cel==7||cel==8) {
			ret = new float[2];
			ret[0] = -SIN_45;
			ret[1] = -SIN_45;
		}
		else if(cel==10||cel==11) {
			ret = new float[2];
			ret[0] = 0;
			ret[1] = -1;
		}
		else if(cel==12||cel==13) {
			ret = new float[2];
			ret[0] = -1;
			ret[1] = 0;
		}
		return ret;
	}

	public void iniciaNitro() {
		if(!nitro) {
			nitro = true;
			posicao_inicial[0] = corpo.getRefPixelX();
			posicao_inicial[1] = corpo.getRefPixelY();
			aceleracao[0] = 0;
			aceleracao[1] = 0;
			velocidade_inicial[0] = orientacao[0]*VELOCIDADE_COM_NITRO;
			velocidade_inicial[1] = orientacao[1]*VELOCIDADE_COM_NITRO;
			tempo[0] = 0;
			tempo[1] = 0;
			tempoNitro = 0;
		}
	}

	private void trataColisaoEspermicida() {
		if(!colisaoEspermicida) {
			colisaoEspermicida = true;  //Marcar que está sendo setado
			tempo[0] = 0; //Zerar o tempo
			tempo[1] = 0;
			posicao_inicial[0] = corpo.getRefPixelX(); //setar Posição inicial igual a 
			posicao_inicial[1] = corpo.getRefPixelY(); //Pixel de Referência do corpo
			velocidade_inicial[0] = orientacao[0]*VELOCIDADE_ESPERMICIDA;//E a velocidade inicial fica
			velocidade_inicial[1] = orientacao[1]*VELOCIDADE_ESPERMICIDA; //Orientação*Velocidade no Espermicida
			velocidade[0] = orientacao[0]*VELOCIDADE_ESPERMICIDA;
			velocidade[1] = orientacao[1]*VELOCIDADE_ESPERMICIDA;
			subrotinaAtualizaAceleracao(orientacao);
		}
	}

	private void trataDescolisaoEspermicida() {
		colisaoEspermicida = false;
		posicao_inicial[0] = corpo.getRefPixelX();
		posicao_inicial[1] = corpo.getRefPixelY();
		tempo[0] = 0;
		tempo[1] = 0;
		subrotinaAtualizaAceleracao(orientacao);
	}

	private void subrotinaAtualizaAceleracao(float or[]) {
		float vMax[] = new float[2];
		if(nitro) {
			aceleracao[0] = or[0]*ACELERACAO_COM_NITRO;
			aceleracao[1] = or[1]*ACELERACAO_COM_NITRO;
			vMax[0] = orientacao[0]*VELOCIDADE_COM_NITRO;
			vMax[1] = orientacao[1]*VELOCIDADE_COM_NITRO;
		}else if(colisaoEspermicida) {  //Se está no espermicida
			//COD3
			aceleracao[0] = or[0]*ACELERACAO_ESPERMICIDA; //Aceleração fica em 
			aceleracao[1] = or[1]*ACELERACAO_ESPERMICIDA;//Zero
			vMax[0] = orientacao[0]*VELOCIDADE_ESPERMICIDA; //E a Velocidade Máxima
			vMax[1] = orientacao[1]*VELOCIDADE_ESPERMICIDA; //Para o Espermicida

		}else {
			aceleracao[0] = or[0]*ACELERACAO_SEM_NITRO;
			aceleracao[1] = or[1]*ACELERACAO_SEM_NITRO;
			vMax[0] = orientacao[0]*VELOCIDADE_MAXIMA_SEM_NITRO;
			vMax[1] = orientacao[1]*VELOCIDADE_MAXIMA_SEM_NITRO;
		}
		if((vMax[0]<velocidade_inicial[0]&&vMax[0]>=0&&velocidade_inicial[0]>0)||(vMax[0]>velocidade_inicial[0]&&vMax[0]<=0&&velocidade_inicial[0]<0)) {
			if((aceleracao[0]>0&&velocidade_inicial[0]>0)||(aceleracao[0]<0&&velocidade_inicial[0]<0))
				aceleracao[0] = Math.abs(aceleracao[1]);
			if(aceleracao[0]==0) {
				if(nitro)
					aceleracao[0] = DIRECOES_QUADRANTE[3][0]*ACELERACAO_COM_NITRO;
				else if(colisaoEspermicida)
					aceleracao[0] = DIRECOES_QUADRANTE[3][0]*ACELERACAO_ESPERMICIDA;
				else
					aceleracao[0] = DIRECOES_QUADRANTE[3][0]*ACELERACAO_SEM_NITRO;
			}
			if(velocidade_inicial[0]>0)
				aceleracao[0] = -aceleracao[0];
		}
		if((vMax[1]<velocidade_inicial[1]&&vMax[1]>=0&&velocidade_inicial[1]>0)||(vMax[1]>velocidade_inicial[1]&&vMax[1]<=0&&velocidade_inicial[1]<0)) {
			if((aceleracao[1]>0&&velocidade_inicial[1]>0)||(aceleracao[1]<0&&velocidade_inicial[1]<0))
				aceleracao[1] = Math.abs(aceleracao[0]);
			if(aceleracao[1]==0) {
				if(nitro)
					aceleracao[1] = DIRECOES_QUADRANTE[3][0]*ACELERACAO_COM_NITRO;
				else if(colisaoEspermicida)
					aceleracao[1] = DIRECOES_QUADRANTE[3][0]*ACELERACAO_ESPERMICIDA;
				else
					aceleracao[1] = DIRECOES_QUADRANTE[3][0]*ACELERACAO_SEM_NITRO;
			}
			if(velocidade_inicial[1]>0)
				aceleracao[1] = -aceleracao[1];
		}
	}

	private void atualizaCheckpoint(long tempoAtual,GerenciadorCamadas gc,Pista pista) {
		float distanciaAnt = 0;
		int indice = -1;
		float distancia;
		//Checagem de mudanca para o proximo checkpoint
		if(ckDep!=null) {
			int[] pontoCheckpointAnterior = new int[2];
			if(ckAnt!=null) {
				pontoCheckpointAnterior[0] = ckAnt[0][0][0];
				pontoCheckpointAnterior[1] = ckAnt[0][0][1];
			}
			else {
				pontoCheckpointAnterior[0] = pista.inicioPorcentagemPista()[0];
				pontoCheckpointAnterior[1] = pista.inicioPorcentagemPista()[1];
			}
			int[] pontoCentralProximoCheckpoint = new int[2];
			for(int i=0;i<ckDep.length;i+=1) {
				pontoCentralProximoCheckpoint[0] = (ckDep[i][0][0] + ckDep[i][1][0])/2;
				pontoCentralProximoCheckpoint[1] = (ckDep[i][0][1] + ckDep[i][1][1])/2;
				if(ckDep[i][0][0]!=ckDep[i][1][0]) {
					float aDep = (float)(ckDep[i][0][1] - ckDep[i][1][1])/(float)(ckDep[i][0][0] - ckDep[i][1][0]);
					float bDep = ckDep[i][0][1] - aDep*ckDep[i][0][0];
					if((aDep*pontoCheckpointAnterior[0]+bDep<pontoCheckpointAnterior[1]&&aDep*corpo.getRefPixelX()+bDep>corpo.getRefPixelY())||(aDep*pontoCheckpointAnterior[0]+bDep>pontoCheckpointAnterior[1]&&aDep*corpo.getRefPixelX()+bDep<corpo.getRefPixelY())) {
						distancia = (float)Math.sqrt((corpo.getRefPixelX() - pontoCentralProximoCheckpoint[0])*(corpo.getRefPixelX() - pontoCentralProximoCheckpoint[0]) + (corpo.getRefPixelY() - pontoCentralProximoCheckpoint[1])*(corpo.getRefPixelY() - pontoCentralProximoCheckpoint[1]));
						if(indice==-1||distancia<distanciaAnt) {
							indice = i;
							distanciaAnt = distancia;
						}
					}
				}
				else {
					if((pontoCheckpointAnterior[0]<ckDep[i][0][0]&&corpo.getRefPixelX()>ckDep[i][0][0])||(pontoCheckpointAnterior[0]>ckDep[i][0][0]&&corpo.getRefPixelX()<ckDep[i][0][0])) {
						distancia = (float)Math.sqrt((corpo.getRefPixelX() - pontoCentralProximoCheckpoint[0])*(corpo.getRefPixelX() - pontoCentralProximoCheckpoint[0]) + (corpo.getRefPixelY() - pontoCentralProximoCheckpoint[1])*(corpo.getRefPixelY() - pontoCentralProximoCheckpoint[1]));
						if(indice==-1||distancia<distanciaAnt) {
							indice = i;
							distanciaAnt = distancia;
						}
					}
				}
			}
			if(indice!=-1)
				atualizaCheckpointAfrente(indice,tempoAtual,gc,pista);
		}
		//Checagem de mudanca para o checkpoint anterior
		if(ckAnt!=null) {
			int[] pontoProximoCheckpoint = new int[2];
			if(ckDep!=null) {
				pontoProximoCheckpoint[0] = ckDep[0][0][0];
				pontoProximoCheckpoint[1] = ckDep[0][0][1];
			}
			distanciaAnt = 0;
			indice = -1;
			int[] pontoCentralCheckpointAnterior = new int[2];
			for(int i=0;i<ckAnt.length;i+=1) {
				pontoCentralCheckpointAnterior[0] = (ckAnt[i][0][0] + ckAnt[i][1][0])/2;
				pontoCentralCheckpointAnterior[1] = (ckAnt[i][0][1] + ckAnt[i][1][1])/2;
				if(ckAnt[i][0][0]!=ckAnt[i][1][0]) {
					float aAnt = (float)(ckAnt[i][0][1] - ckAnt[i][1][1])/(float)(ckAnt[i][0][0] - ckAnt[i][1][0]);
					float bAnt = ckAnt[i][0][1] - aAnt*ckAnt[i][0][0];
					if((aAnt*pontoProximoCheckpoint[0]+bAnt<pontoProximoCheckpoint[1]&&aAnt*corpo.getRefPixelX()+bAnt>corpo.getRefPixelY())||(aAnt*pontoProximoCheckpoint[0]+bAnt>pontoProximoCheckpoint[1]&&aAnt*corpo.getRefPixelX()+bAnt<corpo.getRefPixelY())) {
						distancia = (float)Math.sqrt((corpo.getRefPixelX() - pontoCentralCheckpointAnterior[0])*(corpo.getRefPixelX() - pontoCentralCheckpointAnterior[0]) + (corpo.getRefPixelY() - pontoCentralCheckpointAnterior[1])*(corpo.getRefPixelY() - pontoCentralCheckpointAnterior[1]));
						if(indice==-1||distancia<distanciaAnt) {
							indice = i;
							distanciaAnt = distancia;
						}
					}
				}
				else {
					if((pontoProximoCheckpoint[0]<ckAnt[i][0][0]&&corpo.getRefPixelX()>ckAnt[i][0][0])||(pontoProximoCheckpoint[0]>ckAnt[i][0][0]&&corpo.getRefPixelX()<ckAnt[i][0][0])) {
						distancia = (float)Math.sqrt((corpo.getRefPixelX() - pontoCentralCheckpointAnterior[0])*(corpo.getRefPixelX() - pontoCentralCheckpointAnterior[0]) + (corpo.getRefPixelY() - pontoCentralCheckpointAnterior[1])*(corpo.getRefPixelY() - pontoCentralCheckpointAnterior[1]));
						if(indice==-1||distancia<distanciaAnt) {
							indice = i;
							distanciaAnt = distancia;
						}
					}
				}
			}
			if(indice!=-1)
				atualizaCheckpointAtras(indice,pista);
		}
	}

	private void atualizaCheckpointAfrente(int indice,long tempoAtual,GerenciadorCamadas gc,Pista pista) {
		if(iCkDep[indice]==pista.retornaPorcentagemPista()[contCheckpoint]) {
			contCheckpoint += 1;
			if(contCheckpoint==pista.retornaPorcentagemPista().length)
				contCheckpoint = 0;
			int[] pontoCentralAnt = {pontoCentralContagemCheckpoint[0],pontoCentralContagemCheckpoint[1]};
			short[][] proximoCheckpoint = pista.retornaCheckpoint(pista.retornaPorcentagemPista()[contCheckpoint]);
			pontoCentralContagemCheckpoint[0] = (proximoCheckpoint[0][0] + proximoCheckpoint[1][0])/2;
			pontoCentralContagemCheckpoint[1] = (proximoCheckpoint[0][1] + proximoCheckpoint[1][1])/2;
			distanciaMaximaPontoCentral = (float)Math.sqrt((pontoCentralAnt[0] - pontoCentralContagemCheckpoint[0])*(pontoCentralAnt[0] - pontoCentralContagemCheckpoint[0]) + (pontoCentralAnt[1] - pontoCentralContagemCheckpoint[1])*(pontoCentralAnt[1] - pontoCentralContagemCheckpoint[1]));
			if(pista.fazVoltas()&&contCheckpoint==1) {
				if(flagContaVolta)
					contVoltas += 1;
				flagContaVolta = true;
			}
		}
		if(ckDep[indice][3].length==0||contVoltas==3) {
			//Fim da corrida
			posicao_inicial[0] += (int)((2000*velocidade_inicial[0]*tempo[0] + aceleracao[0]*tempo[0]*tempo[0])/2000000);
			posicao_inicial[1] += (int)((2000*velocidade_inicial[1]*tempo[1] + aceleracao[1]*tempo[1]*tempo[1])/2000000);
			velocidade[0] = (1000*velocidade_inicial[0] + aceleracao[0]*tempo[0])/1000;
			velocidade[1] = (1000*velocidade_inicial[1] + aceleracao[1]*tempo[1])/1000;
			velocidade_inicial[0] = velocidade[0];
			velocidade_inicial[1] = velocidade[1];
			tempo[0] = 0;
			tempo[1] = 0;
			float a2grau;
			float b2grau;
			float c2grau;
			if(ckDep[indice][0][0]!=ckDep[indice][1][0]) {
				float a = (float)(ckDep[indice][0][1] - ckDep[indice][1][1])/(float)(ckDep[indice][0][0] - ckDep[indice][1][0]);
				float b = ckDep[indice][0][1] - a*ckDep[indice][0][0];
				a2grau = a*aceleracao[0] - aceleracao[1];
				b2grau = 2*(a*velocidade_inicial[0] - velocidade_inicial[1]);
				c2grau = 2*(a*posicao_inicial[0] - posicao_inicial[1] + b);
			}                                 
			else {
				a2grau = aceleracao[0];
				b2grau = 2*velocidade_inicial[0];
				c2grau = 2*(posicao_inicial[0] - ckDep[indice][0][0]);
			}
			if(a2grau!=0) {
				float delta = (float)Math.sqrt(b2grau*b2grau - 4*a2grau*c2grau);
				float t1 = (-b2grau + delta)/(2*a2grau);
				float t2 = (-b2grau - delta)/(2*a2grau);
				if(t1<0&&(t2>0||(t2<0&&t1>t2)))
					tempoFinalizacao = (long)(t1*1000);
				else if(t2<0&&(t1>0||(t1<0&&t2>t1)))
					tempoFinalizacao = (long)(t2*1000);
			}
			else {
				//A velocidade esta maxima
				float pontoInterseccao[] = new float[2];
				if(ckDep[indice][0][0]!=ckDep[indice][1][0]) {
					float aCk = (float)(ckDep[indice][0][1] - ckDep[indice][1][1])/(float)(ckDep[indice][0][0] - ckDep[indice][1][0]);
					float bCk = ckDep[indice][0][1] - aCk*ckDep[indice][0][0];
					if(velocidade[0]!=0) {
						float aV = velocidade[1]/velocidade[0];
						float bV = posicao_inicial[1] - aV*posicao_inicial[0];
						pontoInterseccao[0] = (bV - bCk)/(aCk - aV);
						pontoInterseccao[1] = aV*pontoInterseccao[0] + bV;
					}
					else {
						pontoInterseccao[0] = posicao_inicial[0];
						pontoInterseccao[1] = aCk*pontoInterseccao[0] + bCk;
					}
				}
				else {
					float aV = velocidade[1]/velocidade[0];
					float bV = posicao_inicial[1] - aV*posicao_inicial[0];
					pontoInterseccao[0] = ckDep[indice][0][0];
					pontoInterseccao[1] = aV*pontoInterseccao[0] + bV;
				}
				float dS = (float)Math.sqrt((pontoInterseccao[0] - posicao_inicial[0])*(pontoInterseccao[0] - posicao_inicial[0]) + (pontoInterseccao[1] - posicao_inicial[1])*(pontoInterseccao[1] - posicao_inicial[1]));
				float v = (float)Math.sqrt(velocidade[0]*velocidade[0] + velocidade[1]*velocidade[1]);
				float t = dS/v;
				tempoFinalizacao = -(long)(t*1000);
			}
			tempoFinalizacao += tempoAtual;
			gc.remove(corpo);
			gc.remove(cauda);
		}
		else {
			r.setSeed(System.currentTimeMillis());
			short indiceProximoCheckpoint = ckDep[indice][3][r.nextInt(ckDep[indice][3].length)];
			short[][] ckEscolhido = pista.retornaCheckpoint(indiceProximoCheckpoint);
			r.setSeed(System.currentTimeMillis()+r.nextInt());
			float por = r.nextFloat();
			pontoCheckpoint[0] = ckEscolhido[0][0] + por*(ckEscolhido[1][0] - ckEscolhido[0][0]);
			pontoCheckpoint[1] = ckEscolhido[0][1] + por*(ckEscolhido[1][1] - ckEscolhido[0][1]);
			short aux[][][] = new short[ckDep[indice][3].length][][];
			for(int i=0;i<ckDep[indice][3].length;i+=1)
				aux[i] = pista.retornaCheckpoint(ckDep[indice][3][i]);
			iCkDep = ckDep[indice][3];
			ckDep = aux;
			Vector v = new Vector();
			for(int i=0;i<ckDep.length;i+=1)
				for(int j=0;j<ckDep[i][2].length;j+=1)
					v.addElement(new Short(ckDep[i][2][j]));
			for(int i=0;i<v.size()-1;i+=1)
				for(int j=i+1;j<v.size();j+=1)
					if(((Short)v.elementAt(i)).shortValue()>((Short)v.elementAt(j)).shortValue()) {
						Short aux2 = (Short)v.elementAt(i);
						v.setElementAt((Short)v.elementAt(j),i);
						v.setElementAt(aux2,j);
					}
			for(int i=0;i<v.size()-1;i+=1)
				if(((Short)v.elementAt(i)).shortValue()==((Short)v.elementAt(i+1)).shortValue()) {
					v.removeElementAt(i+1);
					i-=1;
				}
			if(v.size()>0) {
				iCkAnt = new short[v.size()];
				ckAnt = new short[v.size()][][];
				for(int i=0;i<v.size();i+=1) {
					iCkAnt[i] = ((Short)v.elementAt(i)).shortValue();
					ckAnt[i] = pista.retornaCheckpoint(iCkAnt[i]);
				}
			}
			else {
				iCkAnt = new short[0];
				ckAnt = null;
			}
			System.gc();
		}
	}

	private void atualizaCheckpointAtras(int indice,Pista pista) {
		if(contCheckpoint==0)
			contCheckpoint = pista.retornaPorcentagemPista().length;
		if(iCkAnt[indice]==pista.retornaPorcentagemPista()[contCheckpoint - 1]) {
			int[] pontoCentralAnt = new int[2];
			contCheckpoint -= 1;
			pontoCentralContagemCheckpoint[0] = (ckAnt[indice][0][0] + ckAnt[indice][1][0])/2;
			pontoCentralContagemCheckpoint[1] = (ckAnt[indice][0][1] + ckAnt[indice][1][1])/2;
			if(ckAnt[indice][2].length!=0) {
				int par = contCheckpoint;
				if(contCheckpoint==0) {
					par = pista.retornaPorcentagemPista().length;
					flagContaVolta = false;
				}
				short[][] checkpointAnterior = pista.retornaCheckpoint(pista.retornaPorcentagemPista()[par - 1]);
				pontoCentralAnt[0] = (checkpointAnterior[0][0] + checkpointAnterior[1][0])/2;
				pontoCentralAnt[1] = (checkpointAnterior[0][1] + checkpointAnterior[1][1])/2;
			}
			else {
				pontoCentralAnt[0] = pista.inicioPorcentagemPista()[0];
				pontoCentralAnt[1] = pista.inicioPorcentagemPista()[1];
			}
			distanciaMaximaPontoCentral = (float)Math.sqrt((pontoCentralAnt[0] - pontoCentralContagemCheckpoint[0])*(pontoCentralAnt[0] - pontoCentralContagemCheckpoint[0]) + (pontoCentralAnt[1] - pontoCentralContagemCheckpoint[1])*(pontoCentralAnt[1] - pontoCentralContagemCheckpoint[1]));
		}
		r.setSeed(System.currentTimeMillis());
		float por = r.nextFloat();
		pontoCheckpoint[0] = ckAnt[indice][0][0] + por*(ckAnt[indice][1][0] - ckAnt[indice][0][0]);
		pontoCheckpoint[1] = ckAnt[indice][0][1] + por*(ckAnt[indice][1][1] - ckAnt[indice][0][1]);
		iCkAnt = ckAnt[indice][2];
		if(ckAnt[indice][2].length!=0) {
			short aux[][][] = new short[ckAnt[indice][2].length][][];
			for(int i=0;i<ckAnt[indice][2].length;i+=1)
				aux[i] = pista.retornaCheckpoint(ckAnt[indice][2][i]);
			ckAnt = aux;
			Vector v = new Vector();
			for(int i=0;i<ckAnt.length;i+=1)
				for(int j=0;j<ckAnt[i][3].length;j+=1)
					v.addElement(new Short(ckAnt[i][3][j]));
			for(int i=0;i<v.size()-1;i+=1)
				for(int j=i+1;j<v.size();j+=1)
					if(((Short)v.elementAt(i)).shortValue()>((Short)v.elementAt(j)).shortValue()) {
						Short aux2 = (Short)v.elementAt(i);
						v.setElementAt((Short)v.elementAt(j),i);
						v.setElementAt(aux2,j);
					}
			for(int i=0;i<v.size()-1;i+=1)
				if(((Short)v.elementAt(i)).shortValue()==((Short)v.elementAt(i+1)).shortValue()) {
					v.removeElementAt(i+1);
					i-=1;
				}
			iCkDep = new short[v.size()];
			ckDep = new short[v.size()][][];
			for(int i=0;i<v.size();i+=1) {
				iCkDep[i] = ((Short)v.elementAt(i)).shortValue();
				ckDep[i] = pista.retornaCheckpoint(iCkDep[i]);
			}
		}
		else {
			ckAnt = null;
			ckDep = new short[1][][];
			ckDep[0] = pista.retornaCheckpoint(pista.retornaPorcentagemPista()[0]);
			iCkDep = new short[1];
			iCkDep[0] = pista.retornaPorcentagemPista()[0];
		}
		System.gc();
	}
	
	
	/*
	public void imprimeCheckpoint(Graphics g,int x,int y,int w,int h,Pista pista) {
		g.setColor(0,255,0);
		if(ckDep!=null)
			for(int i=0;i<ckDep.length;i+=1) {
				float p1[] = new float[2];
				float p2[] = new float[2];
				if(ckDep[i][0][0]!=ckDep[i][1][0]) {
					float a = (float)(ckDep[i][0][1] - ckDep[i][1][1])/(ckDep[i][0][0] - ckDep[i][1][0]);
					float b = ckDep[i][0][1] - a*ckDep[i][0][0];
					p1[0] = x;
					p1[1] = a*x+b;
					p2[0] = x+w;
					p2[1] = a*(x+w)+b;
				}
				else {
					p1[0] = ckDep[i][0][0];
					p1[1] = y;
					p2[0] = ckDep[i][0][0];
					p2[1] = y+h;
				}
				g.drawLine((int)p1[0]-x,(int)p1[1]-y,(int)p2[0]-x,(int)p2[1]-y);
				g.fillArc(ckDep[i][0][0]-x-3,ckDep[i][0][1]-y-3,6,6,0,360);
				g.fillArc(ckDep[i][1][0]-x-3,ckDep[i][1][1]-y-3,6,6,0,360);
				g.drawString(String.valueOf(iCkDep[i]),(ckDep[i][0][0]+ckDep[i][1][0])/2 - x,(ckDep[i][0][1]+ckDep[i][1][1])/2-y,Graphics.BOTTOM|Graphics.HCENTER);
			}
		g.setColor(255,255,0);
		if(ckAnt!=null)
			for(int i=0;i<ckAnt.length;i+=1) {
				float p1[] = new float[2];
				float p2[] = new float[2];
				if(ckAnt[i][0][0]!=ckAnt[i][1][0]) {
					float a = (float)(ckAnt[i][0][1] - ckAnt[i][1][1])/(ckAnt[i][0][0] - ckAnt[i][1][0]);
					float b = ckAnt[i][0][1] - a*ckAnt[i][0][0];
					p1[0] = x;
					p1[1] = a*x+b;
					p2[0] = x+w;
					p2[1] = a*(x+w)+b;
				}
				else {
					p1[0] = ckAnt[i][0][0];
					p1[1] = y;
					p2[0] = ckAnt[i][0][0];
					p2[1] = y+h;
				}
				g.drawLine((int)p1[0]-x,(int)p1[1]-y,(int)p2[0]-x,(int)p2[1]-y);
				g.fillArc(ckAnt[i][0][0]-x-3,ckAnt[i][0][1]-y-3,6,6,0,360);
				g.fillArc(ckAnt[i][1][0]-x-3,ckAnt[i][1][1]-y-3,6,6,0,360);
				g.drawString(String.valueOf(iCkAnt[i]),(ckAnt[i][0][0]+ckAnt[i][1][0])/2 - x,(ckAnt[i][0][1]+ckAnt[i][1][1])/2-y,Graphics.BOTTOM|Graphics.HCENTER);
			}
		else
			g.fillArc(pista.inicioPorcentagemPista()[0]-x-3,pista.inicioPorcentagemPista()[1]-y-3,6,6,0,360);
		if(ckAnt!=null&&ckDep!=null) {
			g.setColor(255,255,255);
			for(int i=0;i<ckDep.length;i+=1)
				for(int j=0;j<ckDep[i][2].length;j+=1)
					for(int k=0;k<iCkAnt.length;k+=1)
						if(ckDep[i][2][j]==iCkAnt[k]) {
							g.drawLine(ckAnt[k][0][0]-x,ckAnt[k][0][1]-y,ckDep[i][1][0]-x,ckDep[i][1][1]-y);
							g.drawLine(ckAnt[k][1][0]-x,ckAnt[k][1][1]-y,ckDep[i][0][0]-x,ckDep[i][0][1]-y);
							k = iCkAnt.length;
						}
			g.setColor(255,0,0);
			for(int i=0;i<ckAnt.length;i+=1)
				for(int j=0;j<ckAnt[i][3].length;j+=1)
					for(int k=0;k<iCkDep.length;k+=1)
						if(ckAnt[i][3][j]==iCkDep[k]) {
							g.drawLine(ckAnt[i][0][0]-x,ckAnt[i][0][1]-y,ckDep[k][0][0]-x,ckDep[k][0][1]-y);
							g.drawLine(ckAnt[i][1][0]-x,ckAnt[i][1][1]-y,ckDep[k][1][0]-x,ckDep[k][1][1]-y);
							k = iCkDep.length;
						}
		}
	}*/
}