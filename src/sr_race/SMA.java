package SR_Race;

import Teclas;


//Sistema Multi-Agente
public class SMA {

	private Competidor jogador;
	private int tempoVira;
	private int orientacao;
	private Agente[] agentes;
	private float raioVisao;
	private int nivel; //1 - Facil; 2 - Medio; 3 - Dificil

	public SMA(Competidor c,int o,Agente[] a,int n,int w,int h) {
		jogador = c;
		tempoVira = 0;
		orientacao = o;
		agentes = a;
		raioVisao = Math.min(w,h)/2;
		raioVisao *= raioVisao;
		nivel = n;
	}

	public void entradaJogador(int deltaTempo,int tecla) {
		tempoVira += deltaTempo;
		if ((tecla == Teclas.QUATRO || tecla == Teclas.NAVEGACAOESQUERDA)
				&& tempoVira > 100) {
			orientacao += 1;
			if (orientacao == 16)
				orientacao = 0;
			jogador.mudaOrientacao(orientacao);
			tempoVira = 0;
//			jogador.retornaSprite().move(-5,0);
		} else if ((tecla == Teclas.SEIS || tecla == Teclas.NAVEGACAODIREITA)
				&& tempoVira > 100) {
			orientacao -= 1;
			if (orientacao == -1)
				orientacao = 15;
			jogador.mudaOrientacao(orientacao);
			tempoVira = 0;
//			jogador.retornaSprite().move(5,0);
		}
/*
		else if(tecla==Teclas.DOIS || tecla == Teclas.NAVEGACAOACIMA)
			jogador.retornaSprite().move(0,-5);
		else if(tecla==Teclas.OITO || tecla == Teclas.NAVEGACAOABAIXO)
			jogador.retornaSprite().move(0,5);
		else if(tecla==Teclas.NAVEGACAOMEIO||tecla==Teclas.CINCO)
			System.out.println(jogador.retornaSprite().getRefPixelX()+","+jogador.retornaSprite().getRefPixelY());
*/	}

	public void atualizaSMA(int deltaTempo,long tempoAtual,GerenciadorCamadas gc,Pista pista) {
		//3º - Atualizar os Agentes
		//4º - Mover todos os competidores
		jogador.moveCompetidor(deltaTempo,tempoAtual,gc,pista);
		for(int i=0;i<agentes.length;i+=1)
			if(agentes[i].retornaTempo()==0) {
				agentes[i].atualizaAgente(deltaTempo,nivel,jogador,pista.retornaInimigos(),raioVisao);
				agentes[i].moveCompetidor(deltaTempo,tempoAtual,gc,pista);
			}
	}

	public Competidor retornaJogador() {
		return jogador;
	}

	public Agente[] retornaAgentes() {
		return agentes;
	}
	
	

/*
	public void imprimeVetoresOtimoLocal(Graphics g,int w,int h) {
		g.setColor(0,0,0);
		for(int i=0;i<agentes.length;i+=1)
			g.drawLine(agentes[i].retornaSprite().getRefPixelX()-jogador.retornaSprite().getRefPixelX()+w/2,agentes[i].retornaSprite().getRefPixelY()-jogador.retornaSprite().getRefPixelY()+h/2,agentes[i].retornaSprite().getRefPixelX()-jogador.retornaSprite().getRefPixelX()+w/2+(int)vetoresOtimoLocal[i][0],agentes[i].retornaSprite().getRefPixelY()-jogador.retornaSprite().getRefPixelY()+h/2+(int)vetoresOtimoLocal[i][1]);
	}

	public void imprimeVetoresOtimoGlobal(Graphics g,int w,int h) {
		g.setColor(0,0,0);
		for(int i=0;i<agentes.length;i+=1)
			if(vetoresOtimoGlobal[i]!=null)
				g.drawLine(agentes[i].retornaSprite().getRefPixelX()-jogador.retornaSprite().getRefPixelX()+w/2,agentes[i].retornaSprite().getRefPixelY()-jogador.retornaSprite().getRefPixelY()+h/2,agentes[i].retornaSprite().getRefPixelX()-jogador.retornaSprite().getRefPixelX()+w/2+(int)vetoresOtimoGlobal[i][0],agentes[i].retornaSprite().getRefPixelY()-jogador.retornaSprite().getRefPixelY()+h/2+(int)vetoresOtimoGlobal[i][1]);
	}

	public void imprimeVetoresPessimo(Graphics g,int w,int h) {
		g.setColor(0,0,0);
		for(int i=0;i<agentes.length;i+=1)
			for(int j=0;j<vetoresPessimo[i].length;j+=1)
				if(vetoresPessimo[i][j]!=null)
					g.drawLine(agentes[i].retornaSprite().getRefPixelX()-jogador.retornaSprite().getRefPixelX()+w/2,agentes[i].retornaSprite().getRefPixelY()-jogador.retornaSprite().getRefPixelY()+h/2,agentes[i].retornaSprite().getRefPixelX()-jogador.retornaSprite().getRefPixelX()+w/2+(int)vetoresPessimo[i][j][0],agentes[i].retornaSprite().getRefPixelY()-jogador.retornaSprite().getRefPixelY()+h/2+(int)vetoresPessimo[i][j][1]);
	}*/
}