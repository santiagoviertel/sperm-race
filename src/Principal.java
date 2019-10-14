import java.util.Random;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import SR_Menu.BD;
import SR_Menu.Animacao;
import SR_Menu.Menu;
import SR_Menu.Telas;
import SR_Race.Corrida;

public class Principal extends MIDlet {

	private Thread tr;
	private Screen s;
	private Gerencia g;

	public Principal() {
		g = null;
		tr = null;
	}

	public void startApp() throws MIDletStateChangeException {
		if(g==null) {
			g = new Gerencia(this);
			tr = new Thread(g);
			tr.start();
		}
	}

	public void pauseApp() { }

	public void destroyApp(boolean arg0) throws MIDletStateChangeException {
		notifyDestroyed();
		System.gc();
	}

	public MusicPlayer retornaMusicPlayer() {
		return g.retornaMusicPLayer();
	}

	public byte telaPause() {
		return g.telaPause();
	}

	private class Gerencia implements Runnable {

		private Principal pr;
		private Telas tel;
		private MusicPlayer musica;
		private Corrida c;
		private boolean[] sidequest;//0 para Camisinha / 1 para Rhino
		private short[] opcoes;
		private byte modoJogo;

		private Gerencia(Principal pr) {
			this.pr = pr;
			s = new Screen();
			musica = new MusicPlayer();
			sidequest = new boolean[2];
			opcoes = new short[2];
			modoJogo = -1;
		}

		public void run() {
			/*
			Animacao ani = new Animacao(s);
			s.setarInterface(ani);
			javax.microedition.lcdui.Display.getDisplay(pr).setCurrent(s);
			ani.finalJogo(0, musica);
			 */
			{
				Animacao ani = new Animacao(s);
				s.setarInterface(ani);
				javax.microedition.lcdui.Display.getDisplay(pr).setCurrent(s);
				ani.inicio();
			}
			System.gc();
			tel = new Telas(s,musica);
			boolean loop = true;
			byte personagens[] = null;
			verificaConfiguracoes();
			while(loop) {
				System.gc();
				//Etapa desde o inicio do Menu até a escolha da pista/personagem
				{
					Menu men = new Menu(s,musica);
					s.stopLoad();
					if(opcoes[0]==1)
						musica.musicStart("SR_Menu/SD_Menu.mid",0);
					else if(opcoes[0]==2)
						musica.musicStart("SR_Menu/SD_Menu.mid",33);
					else if(opcoes[0]==3)
						musica.musicStart("SR_Menu/SD_Menu.mid",66);
					else
						musica.musicStart("SR_Menu/SD_Menu.mid",100);

					boolean flag1 = true;
					while(flag1) {
						s.setarInterface(men);
						modoJogo = men.inicia();
						verificaConfiguracoes();
						if(modoJogo==1 || modoJogo==0) {
							s.setarInterface(tel);
							personagens = subrotinaPersonagens();
							if(personagens!=null)
								flag1 = false;
						}
						else {
							flag1 = false;
							loop = false;
						}
					}
				}
				System.gc();
				{
					if(modoJogo==1 || modoJogo==0)
						championship(personagens);
				}
			}
			try {
				destroyApp(true);
			} catch(MIDletStateChangeException e) { }
		}



		private void championship(byte[] personagens){
			int pontos[] = new int[4];
			int indiceJogador = 3;
			for(int i=0;i<pontos.length;i+=1)
				pontos[i] = 0;
			short saida = 1;

			int tempos[];
			//Aqui entra o inicio da corrida
			{
				c = new Corrida(pr,0,personagens,indiceJogador,opcoes[1],s);
				s.stopLoad();
				if(opcoes[0]==1)
					musica.musicStart("SR_Race/SD_Race.mid",0);
				else if(opcoes[0]==2)
					musica.musicStart("SR_Race/SD_Race.mid",33);
				else if(opcoes[0]==3)
					musica.musicStart("SR_Race/SD_Race.mid",66);
				else
					musica.musicStart("SR_Race/SD_Race.mid",100);
				s.setarInterface(tel);
				tel.iniciaCorrida();
				s.setarInterface(c);
				saida = c.inicia();
				if(saida==4) {
					try {
						destroyApp(true);
					} catch (MIDletStateChangeException e) { }
				}
				s.load();
				tempos = c.retornaTempos();
				c = null;
			}
			System.gc();
			byte personagensCorrida[] = new byte[personagens.length];
			int indices[] = new int[personagens.length];
			byte personagensAnt[] = new byte[personagens.length];
			int pontosAnt[] = new int[personagens.length];
			int i,j;
			int aux;
			for(i=0;i<personagens.length;i+=1) {
				personagensCorrida[i] = personagens[i];
				indices[i] = i;
				personagensAnt[i] = personagens[i];
				pontosAnt[i] = pontos[i];
			}
			boolean recorde = verificaRecorde(tempos[indiceJogador],4,"championship");
			for(i=0;i<personagens.length-1;i+=1)
				for(j=i+1;j<personagens.length;j+=1)
					if(tempos[i]>tempos[j]) {
						aux = tempos[i];
						tempos[i] = tempos[j];
						tempos[j] = aux;
						aux = personagensCorrida[i];
						personagensCorrida[i] = personagensCorrida[j];
						personagensCorrida[j] = (byte)aux;
						aux = indices[i];
						indices[i] = indices[j];
						indices[j] = aux;
					}
			pontos[indices[0]] += 10;
			pontos[indices[1]] += 7;
			pontos[indices[2]] += 4;

			for(i=0;i<tempos.length;i+=1)
				tempos[i] /= 1000;
			for(i=0;i<pontos.length-1;i+=1)
				for(j=i+1;j<pontos.length;j+=1)
					if(pontos[j]>pontos[i]) {
						aux = pontos[i];
						pontos[i] = pontos[j];
						pontos[j] = aux;
						aux = personagens[i];
						personagens[i] = personagens[j];
						personagens[j] = (byte)aux;
						if(i==indiceJogador)
							indiceJogador = j;
						else if(j==indiceJogador)
							indiceJogador = i;
					}
			s.stopLoad();
			if(opcoes[0]==1)
				musica.musicStart("SR_Menu/SD_Menu.mid",0);
			else if(opcoes[0]==2)
				musica.musicStart("SR_Menu/SD_Menu.mid",33);
			else if(opcoes[0]==3)
				musica.musicStart("SR_Menu/SD_Menu.mid",66);
			else
				musica.musicStart("SR_Menu/SD_Menu.mid",100);
			s.setarInterface(tel);
			//Resultado da Corrida
			if(recorde)
				tel.mostraPontuacaoCampeonato(personagensCorrida,tempos,indiceJogador,false);
			else
				tel.mostraPontuacaoCampeonato(personagensCorrida,tempos,-1,false);
			//Tabela anterior do Campeonato
			tel.mostraPontuacaoCampeonato(personagensAnt,pontosAnt,-1,true);
			//Tabela posterior do Campeonato
			tel.mostraPontuacaoCampeonato(personagens,pontos,-1,true);

			if(pontos[indiceJogador]==40 && opcoes[1]>=2 && !sidequest[0]) {
				sidequest[0] = true;
				sideQuestCompleto();
			}
			if(pontos[indiceJogador]==50 && opcoes[1]==3 && !sidequest[1]) {
				sidequest[1] = true;
				sideQuestCompleto();
			}
			try{
				Animacao ani = new Animacao(s);
				s.setarInterface(ani);
				ani.finalJogo(personagens[0],musica);

			}catch(OutOfMemoryError e){	}
		}

		private byte[] subrotinaPersonagens() {
			byte[] ret = new byte[4];
			Random r = new Random(System.currentTimeMillis());
			ret[3] = tel.escolhaPersonagem(sidequest[1]);
			if(ret[3]>=0) {
				s.load();
				for(int i=ret.length-2;i>=0;i-=1) {
					ret[i] = (byte)r.nextInt(5);
					r.setSeed(r.nextInt());
					for(int j=ret.length-1;j>i;j-=1)
						if(ret[i]==ret[j]) {
							i += 1;
							j = i;
						}
				}
				return ret;
			}
			return null;
		}

		private byte telaPause() {
			byte opcao;
			s.setarInterface(tel);
			opcao = tel.pausaCorrida(modoJogo==0);
			s.setarInterface(c);
			return opcao;
		}

		private MusicPlayer retornaMusicPLayer() {
			return musica;
		}

		private void sideQuestCompleto() {
			BD banco = new BD("configuracoes",1);
			banco.editarSideQuest(sidequest);
			banco.fechar();
		}

		private void verificaConfiguracoes() {
			BD banco = new BD("configuracoes",1);
			short[] configuracoes = banco.verificarConfiguracoes();
			banco.fechar();
			sidequest = new boolean[2];
			if(configuracoes[0]==0)
				sidequest[0] = false;
			else
				sidequest[0] = true;
			if(configuracoes[1]==0)
				sidequest[1] = false;
			else
				sidequest[1] = true;
			opcoes[0] = configuracoes[2];
			opcoes[1] = configuracoes[3];
		}

		private boolean verificaRecorde(long t, int pista, String bd) {
			boolean retorno = false;
			BD banco = new BD(bd,0);
			short[] tempos = banco.leRecordes();
			int tempoBanco = tempos[pista-1];
			int tempoUsuario = (int)t/1000;
			if(tempoUsuario < tempoBanco) {
				retorno = true;
				tempos[pista-1] = (short)tempoUsuario;
				banco.editarDados(tempos);
			}
			banco.fechar();
			return retorno;
		}
	}
}