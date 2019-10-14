package SR_Race;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import Principal;
import Screen;
import Teclas;
import ScreenInterface;

public class Corrida implements ScreenInterface {

	private Principal pr;
	private Pista pista;
	private GerenciadorCamadas lm;
	private SMA sma;
	private Image contramao;
	private int[] tempos;
	private int indiceJogador;
	private Screen s;
	private int tecla;
	private int inicioPorcentagemPista;
	private int fimPorcentagemPista;
	private int xPorcentagemPista;
	private int largada;
	private Sprite sinal;
	private int[][] ref;
	private long t;
	private long tempo;
	private long tempoInicializacao;
	private long tempoPause; //Verifica o tempo pausado
	private boolean pauseVoluntaria;

	public Corrida(Principal pr,int pista,byte[] pers,int ind,int nivel,Screen s) {
		this.s = s;
		this.pr = pr;
		lm = new GerenciadorCamadas();
		this.pista = new Pista(pista,s.getWidth(),s.getHeight());
		//Adicionar as posicoes iniciais e orientacoes iniciais
		int orientacao = this.pista.retornaOrientacaoInicial();
		int[][] posicoesIniciais = this.pista.pontosPartida();
		Competidor jogador = new Competidor(posicoesIniciais[ind][0],posicoesIniciais[ind][1],orientacao,pers[ind],this.pista);
		jogador.adicionaCamada(lm);
		Agente[] agentes = new Agente[pers.length - 1];
		for(int i=0;i<pers.length;i+=1) {
			if(i<ind) {
				agentes[i] = new Agente(posicoesIniciais[i][0],posicoesIniciais[i][1],orientacao,pers[i],this.pista);
				agentes[i].adicionaCamada(lm);
			}
			else if(i>ind) {
				agentes[i-1] = new Agente(posicoesIniciais[i][0],posicoesIniciais[i][1],orientacao,pers[i],this.pista);
				agentes[i-1].adicionaCamada(lm);
			}
		}
		sma = new SMA(jogador,orientacao,agentes,nivel,s.getWidth(),s.getHeight());
		try {
			contramao = Image.createImage("/SR_Race/IM_WrongWay.png");
			sinal = new Sprite(Image.createImage("/SR_Race/IM_TrafficLight.png"),55,105);
		}catch(IOException e) { }
		this.pista.adicionaCamada(lm);
		indiceJogador = ind;
		inicioPorcentagemPista = (int)(0.95*s.getHeight());
		fimPorcentagemPista = (int)(0.05*s.getHeight());
		xPorcentagemPista = (int)(0.05*s.getWidth());
		ref = new int[2][2];
		ref[0][0] = s.getWidth()/2 - 55;
		ref[0][1] = -105;	
		ref[1][0] = s.getWidth()/2;
		ref[1][1] = -105;
		largada = 0;
		tempoPause = 0;
	}

	public byte inicia() {
		int deltaTempo, t1 = 0;
		int i,j;
		byte retornaPause=1;
		Competidor primeiro;
		Competidor jogador = sma.retornaJogador();
		Agente[] agentes = sma.retornaAgentes();
		System.gc();
		tempoInicializacao = System.currentTimeMillis();
		tempo = tempoInicializacao;
		while(t1 <= 1000) {
			ref[0][1] = (int)(-115 + 0.105*(float)t1);
			ref[1][1] = ref[0][1];
			pista.atualizaPista(jogador.retornaSprite(),t1,jogador.retornaSprite().getRefPixelX() - s.getWidth()/2,jogador.retornaSprite().getRefPixelY() - s.getHeight()/2);
			lm.setarJaneladeVisualizacao(jogador.retornaSprite().getRefPixelX() - s.getWidth()/2,jogador.retornaSprite().getRefPixelY() - s.getHeight()/2,s.getWidth(),s.getHeight());
			synchronized(this) {
				try {
					s.repaint();
					wait();
				}catch(InterruptedException e) { }
			}
			t1 = (int)(System.currentTimeMillis()-tempo);
		}
		ref[0][1] = 0;
		ref[1][1] = 0;
		try {
			synchronized(this) {
				s.repaint();
				wait();
			}
			Thread.sleep(1000);
		}catch(InterruptedException e1) { }

		while(jogador.retornaTempo()==0 && retornaPause==1) {
			t = System.currentTimeMillis();
			deltaTempo = (int)(t - tempo);
			if(tempoPause==0) {
				t1 = (int)(t - tempoInicializacao);
				if (t1 >= 4000) {  //Se já largou
					if(t1 <= 5000) { //Se ainda está no 3º segundo
						int dist;
						largada = 3;//Exiba GO!
						int B = s.getWidth()/2 - 50;
						dist = (int) (ref[0][0]- ((-(B+50.0f)/1000)*t1+5*B+200.0f));
						ref[0][0] -= dist; 
						ref[1][0] += dist;
					}
					else //Senão
						largada = 4; //Não Exiba nada

					 //Modifica a orientacao conforme a entrada do jogador
					 sma.entradaJogador(deltaTempo,tecla);

					 //1º - Colisao com nitro
					 if(pista.pegouNitro(jogador.retornaSprite()))
						 jogador.iniciaNitro();
					 for(i=0;i<agentes.length;i+=1)
						 if(pista.pegouNitro(agentes[i].retornaSprite()))
							 agentes[i].iniciaNitro();

					 //2º - Colisao com espermicida
					 jogador.colisaoEpermicida(pista.retornaInimigos());
					 for(i=0;i<agentes.length;i+=1)
						 agentes[i].colisaoEpermicida(pista.retornaInimigos());

					 //3º e 4ª - Atualiza o sistema Multi-Agente
					 sma.atualizaSMA(deltaTempo,tempo,lm,pista);

					 //5º - Colisao com Inimigos
					 if(pista.retornaInimigos()!=null) {
						 //Competidor
						 if(jogador.retornaSprite().collidesWith(pista.retornaInimigos().retornaDiafragma(),false))
							 if(jogador.retornaSprite().collidesWith(pista.retornaInimigos().retornaDiafragma(),true))
								 jogador.trataColisaoDiafragma(pista.retornaInimigos());
						 if(jogador.retornaSprite().collidesWith(pista.retornaInimigos().retornaExterminador(),false))
							 if(jogador.retornaSprite().collidesWith(pista.retornaInimigos().retornaExterminador(),true))
								 jogador.trataColisaoExterminador(pista.retornaInimigos());
						 //Agentes
						 for(i=0;i<agentes.length;i+=1) {
							 if(agentes[i].retornaSprite().collidesWith(pista.retornaInimigos().retornaDiafragma(),false))
								 if(agentes[i].retornaSprite().collidesWith(pista.retornaInimigos().retornaDiafragma(),true))
									 if(agentes[i].retornaTempo()==0)
										 agentes[i].trataColisaoDiafragma(pista.retornaInimigos());
							 if(agentes[i].retornaSprite().collidesWith(pista.retornaInimigos().retornaExterminador(),false))
								 if(agentes[i].retornaSprite().collidesWith(pista.retornaInimigos().retornaExterminador(),true))
									 if(agentes[i].retornaTempo()==0)
										 agentes[i].trataColisaoExterminador(pista.retornaInimigos());
						 }
					 }

					 //6º - Colisao entre os Competidores
					 for(i=0;i<agentes.length;i+=1)
						 if(jogador.retornaSprite().collidesWith(agentes[i].retornaSprite(),false))
							 if(jogador.retornaSprite().collidesWith(agentes[i].retornaSprite(),true))
								 if(agentes[i].retornaTempo()==0)
									 jogador.trataColisaoCompetidores(agentes[i]);
					 for(i=0;i<agentes.length-1;i+=1)
						 for(j=i+1;j<agentes.length;j+=1)
							 if(agentes[i].retornaSprite().collidesWith(agentes[j].retornaSprite(),false))
								 if(agentes[i].retornaSprite().collidesWith(agentes[j].retornaSprite(),true))
									 if(agentes[i].retornaTempo()==0&&agentes[j].retornaTempo()==0)
										 agentes[i].trataColisaoCompetidores(agentes[j]);

					 //7º - Colisao com a pista
					 jogador.colisaoPista(pista);
					 for(i=0;i<agentes.length;i+=1)
						 agentes[i].colisaoPista(pista);
				}
				else { //Se não saiu a partida
					if(t1 <= 2000) //Se falta dois segundos
						largada = 0;
					else if(t1 <= 3000) //Senão falta 1 segundo
						largada = 1;
					else
						largada = 2;
				}

				primeiro = jogador;
				for(i=0;i<agentes.length;i+=1)
					if(agentes[i].retornaPorcentagemPista(pista)>primeiro.retornaPorcentagemPista(pista))
						primeiro = agentes[i];
				pista.atualizaPista(primeiro.retornaSprite(),deltaTempo,jogador.retornaSprite().getRefPixelX() - s.getWidth()/2,jogador.retornaSprite().getRefPixelY() - s.getHeight()/2);
				lm.setarJaneladeVisualizacao(jogador.retornaSprite().getRefPixelX() - s.getWidth()/2,jogador.retornaSprite().getRefPixelY() - s.getHeight()/2,s.getWidth(),s.getHeight());
				synchronized(this) {
					try {
						s.repaint();
						wait();
					}catch(InterruptedException e) { }
				}
				tempo = t;
			}
			else
				retornaPause = sairPause();
		}
		int indice = 0;
		tempos = new int[agentes.length + 1];
		for(i=0;i<tempos.length;i+=1) {
			if(i<indiceJogador) {
				if(agentes[i].retornaTempo()!=0)
					tempos[i] = (int)(agentes[i].retornaTempo() - tempoInicializacao) - 5000;
				else
					tempos[i] = (int)(agentes[i].retornaTempo(tempo,pista) - tempoInicializacao) - 5000;
			}
			else if(i>indiceJogador) {
				if(agentes[i-1].retornaTempo()!=0)
					tempos[i] = (int)(agentes[i-1].retornaTempo() - tempoInicializacao) - 5000;
				else
					tempos[i] = (int)(agentes[i-1].retornaTempo(tempo,pista) - tempoInicializacao) - 5000;
			}
			else{
				tempos[i] = (int)(jogador.retornaTempo() - tempoInicializacao) - 5000;
				indice = i;
			}
		}
		if(retornaPause!=1)
			tempos[indice] = Integer.MAX_VALUE;

		return retornaPause;
	}

	public synchronized void paint(Graphics g) {
		Competidor jogador = sma.retornaJogador();
		Agente[] agentes = sma.retornaAgentes();
		lm.pintar(g);
		pista.pintaBasePlacas(g,jogador.retornaSprite().getRefPixelX()-s.getWidth()/2,jogador.retornaSprite().getRefPixelY()-s.getHeight()/2,s.getWidth(),s.getHeight());
		
//		pista.imprimeCheckpointInicioFim(g,jogador.retornaSprite().getRefPixelX()-s.getWidth()/2,jogador.retornaSprite().getRefPixelY()-s.getHeight()/2,s.getWidth(),s.getHeight());

//		jogador.imprimeCheckpoint(g,jogador.retornaSprite().getRefPixelX()-s.getWidth()/2,jogador.retornaSprite().getRefPixelY()-s.getHeight()/2,s.getWidth(),s.getHeight(),pista);

		//Verificacao de contramao
		if(jogador.contraMao())
			g.drawImage(contramao,s.getWidth()/2,5,Graphics.TOP|Graphics.HCENTER);

		g.setColor(255,255,255);
		g.drawLine(xPorcentagemPista,inicioPorcentagemPista,xPorcentagemPista,fimPorcentagemPista);
		g.drawLine(xPorcentagemPista - 5,inicioPorcentagemPista,xPorcentagemPista + 5,inicioPorcentagemPista);
		g.drawLine(xPorcentagemPista - 5,fimPorcentagemPista,xPorcentagemPista + 5,fimPorcentagemPista);
		//Impressao das porcentagens de cada personagem
		float por;
		g.setColor(255,0,0);
		for(int i=0;i<agentes.length;i+=1)
			if(agentes[i].retornaTempo()==0) {
				por = agentes[i].retornaPorcentagemPista(pista);
				por = inicioPorcentagemPista + por*(fimPorcentagemPista - inicioPorcentagemPista);
				g.fillArc(xPorcentagemPista - 3,(int)(por - 3),6,6,0,360);
			}
		g.setColor(0,255,0);
		por = jogador.retornaPorcentagemPista(pista);
		por = inicioPorcentagemPista + por*(fimPorcentagemPista - inicioPorcentagemPista);
		g.fillArc(xPorcentagemPista - 3,(int)(por - 3),6,6,0,360);

		switch(largada) {
		case 0:
			sinal.setPosition(ref[0][0], ref[0][1]);
			sinal.setFrame(0);
			sinal.paint(g);
			sinal.setPosition(ref[1][0], ref[1][1]);
			sinal.setFrame(1);
			sinal.paint(g);
			break;
		case 1:
			sinal.setPosition(ref[0][0], ref[0][1]);
			sinal.setFrame(2);
			sinal.paint(g);
			sinal.setPosition(ref[1][0], ref[1][1]);
			sinal.setFrame(3);
			sinal.paint(g);
			break;
		case 2:
			sinal.setPosition(ref[0][0], ref[0][1]);
			sinal.setFrame(4);
			sinal.paint(g);
			sinal.setPosition(ref[1][0], ref[1][1]);
			sinal.setFrame(5);
			sinal.paint(g);
			break;
		case 3:
			sinal.setPosition(ref[0][0], ref[0][1]);
			sinal.setFrame(6);
			sinal.paint(g);
			sinal.setPosition(ref[1][0], ref[1][1]);
			sinal.setFrame(7);
			sinal.paint(g);
			break;
		}
		notify();
	}

	public int[] retornaTempos() {
		return tempos;
	}

	private byte sairPause() {
		byte opcao;
		if(!pauseVoluntaria)
			pr.retornaMusicPlayer().musicContinue();
		opcao = pr.telaPause();
		tempoPause = System.currentTimeMillis() - tempoPause;
		t += tempoPause;
		tempo += tempoPause;
		tempoInicializacao += tempoPause;
		tempoPause = 0;
		return opcao;
	}

	public void keyPressed(int t) {
		if(t!=Teclas.SOFTBUTTONDIREITO)
			tecla = t;
		else {
			//Entra em Pause
			tempoPause = System.currentTimeMillis();
			pauseVoluntaria = true;
		}
	}

	public void keyReleased(int t) {
		tecla = 0;
	}

	public void showNotify() { }

	public void hideNotify() {
		tempoPause = System.currentTimeMillis();
		pr.retornaMusicPlayer().musicPause();
		pauseVoluntaria = false;
	}
	
}