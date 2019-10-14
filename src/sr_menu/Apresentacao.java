package SR_Menu;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import java.io.IOException;
import java.util.Vector;

public class Apresentacao {

	//C O N S T A N T E S
	private int PIXELS_Y;		            //Constante da quantidade de pixels para letra na vertical

	//V A R I A V E I S
	private int x;							//Constante X da posicao
	private int y;							//Constante Y da posicao
	private int w;							//Constante de largura do quadrado interno
	private int h;							//Constante de altura do quadrado interno
	private int fim;						//Variavel que marca a string final a ser mostrada
	private int inicio;						//Variavel que marca a string inicial a ser mostrada
	private int ret;						//Variavel que retorna a pagina anterior 
	private Sprite botaoDir;				//Botao inferior Direito
	private Sprite botaoEsq;				//Botao inferior Esquerdo
	private StringBuffer[] s;				//Nome do personagem e Strings mostradas na tela

	//M E T O D O S
	//Argumentos: (<posicao Y inicial>,<largura da tela>,<altura da tela>)
	public Apresentacao(int x,int y,int w,int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		PIXELS_Y = Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_PLAIN,Font.SIZE_MEDIUM).getHeight();
		try {
			botaoDir = new Sprite(Image.createImage("/SR_Menu/IM_Navigation.png"),7,7);
			botaoDir.setPosition(((int)(w/2))-botaoDir.getWidth(),h-botaoDir.getHeight());
			botaoDir.setFrame(0);
			botaoEsq = new Sprite(Image.createImage("/SR_Menu/IM_Navigation.png"),7,7);
			botaoEsq.setPosition(((int)(w/2)),h-botaoEsq.getHeight());
			botaoEsq.setFrame(1);
		} catch (IOException e) { }
	}

	//Metodo que seta a fala a ser mostrada na tela
	//Admite mudanca de linha atraves do caracter '£'
	public void setarString(String s) {
		int i;
		int in = 0,fi = 0,aux = 1;
		{
			Vector vAux = new Vector();
			for(i=0;s.charAt(i)!=':';i+=1){}		//Inicializa a lista com o nome
			vAux.addElement(s.substring(0,i));
			s = s.substring(i+1,s.length());		//Limpa o nome da String
			while(in<s.length()) {
				for(i=0;in+i<s.length()&&Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_PLAIN,Font.SIZE_MEDIUM).substringWidth(s,in,i)<w;i+=1) {
					if(s.charAt(in+i)==' ')
						aux = i;
					else if(s.charAt(in+i)=='£') {
						aux = i;
						break;
					}
				}
				if(in+i!=s.length())
					fi = in + aux;
				else
					fi = s.length();
				vAux.addElement(s.substring(in,fi));
				in = fi+1;
			}
			this.s = new StringBuffer[vAux.size()];
			for(i=0;i<vAux.size();i+=1)
				this.s[i] = new StringBuffer((String)vAux.elementAt(i));
		}
		inicio = 1;
		System.gc();
	}

	//Metodo que anima a fala na tela
	public void animaConversacao(Graphics g) {
		//Quadrado de fundo
		int yf = y + h;
		//Nome do personagem*/
		g.setFont(Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
		g.drawString(s[0].toString(),x - (int)(g.getFont().stringWidth(s[0].toString())/2),y+7,Graphics.TOP|Graphics.LEFT);
		//Texto
		g.setFont(Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_PLAIN,Font.SIZE_MEDIUM));
		int tamY,i,aux=1;
		tamY = y + 12 + aux*PIXELS_Y;
		for(i=inicio;tamY + 2*PIXELS_Y < y + yf && i<s.length;i+=1) { 	//Espaco para o botao de proxima tela
			g.drawString(s[i].toString(),x - (int)(g.getFont().stringWidth(s[i].toString())/2),tamY,Graphics.TOP|Graphics.LEFT);
			aux += 1;
			tamY = y + 12 + aux*PIXELS_Y;
		}
		fim = aux;
		if (inicio!=1)
			botaoDir.paint(g);
		if (inicio + fim -1 < s.length)
			botaoEsq.paint(g);
	}

	//Metodo que inicia o proximo bloco de texto a ser mostrado na tela
	public void proximoTexto() {
		if(inicio + fim -1 < s.length) {
			inicio += fim-1;
			ret = fim-1;
		}
	}

	//Metodo que inicia o bloco de texto a ser mostrado anteriormente na tela
	public void textoAnterior() {
		if(inicio!=1)
			inicio -= ret;
	}

	//Metodo que verifica se ja foi apresentado todo o texto
	public boolean textoApresentado() {
		if(inicio + fim -1 < s.length)
			return false;
		return true;
	}
}