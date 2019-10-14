package SR_Race;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Layer;

public class GerenciadorCamadas {

	private Layer[] camadas;
	private int[] pontoInicial;
	private int[] tamanho;

	public GerenciadorCamadas() {
		pontoInicial = new int[2];
		tamanho = new int[2];
		camadas = null;
	}

	public void adiciona(Layer l) {
		int qtd = 1;
		if(camadas!=null)
			qtd = camadas.length + 1;
		Layer[] aux = new Layer[qtd];
		aux[0] = l;
		for(int i=0;i<qtd-1;i+=1)
			aux[i+1] = camadas[i];
		camadas = aux;
		System.gc();
	}

	public void remove(Layer l) {
		{
			Layer aux[] = new Layer[camadas.length-1];
			for(int i=0,j=0;j<camadas.length;i+=1,j+=1) {
				if(l==camadas[j])
					i -= 1;
				else
					aux[i] = camadas[j];
			}
			camadas = aux;
		}
		System.gc();
	}

	public void setarJaneladeVisualizacao(int x,int y,int width,int height) {
		pontoInicial[0] = x;
		pontoInicial[1] = y;
		tamanho[0] = width;
		tamanho[1] = height;
	}

	public void pintar(Graphics g) {
		for(int i=0;i<camadas.length;i+=1)
			if((camadas[i].getX()<=pontoInicial[0]+tamanho[0])&&(camadas[i].getX()+camadas[i].getWidth()>pontoInicial[0]))
				if((camadas[i].getY()<=pontoInicial[1]+tamanho[1])&&(camadas[i].getY()+camadas[i].getHeight()>pontoInicial[1])) {
					camadas[i].move(-pontoInicial[0],-pontoInicial[1]);
					camadas[i].paint(g);
					camadas[i].move(pontoInicial[0],pontoInicial[1]);
				}
	}
}