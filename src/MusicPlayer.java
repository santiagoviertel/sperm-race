import java.io.IOException;
import java.io.InputStream;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.Manager;
import javax.microedition.media.control.VolumeControl;

public class MusicPlayer {

	private Player mc_player; //Objeto que executar� a m�sica
	private VolumeControl vc; //Objeto que Controla o volume
	//private int volume; //Atributo que armazenar� o volume 

	public MusicPlayer() { }

	public void musicPause() {
		try { //Tente parar a m�sica
			mc_player.stop();
		} catch (MediaException e) { //Se n�o der, ignora (geralmente cai aqui quando o 
			//player n�o est� tocando)
		}
	}

	public void musicContinue() {
		try { //Tente iniciar a m�sica (sempre continuar� no ponto em que parou) 
			mc_player.start();
		} catch (MediaException e) { //Se n�o conseguir
			//Falhou...
		}
	}

	public void musicStart(String music,int volume) {
		InputStream is; //Local onde a m�sica estar�
		
		if(mc_player!=null){
			
				mc_player.close();
			
		}
    
		//Iniciar� a inst�ncia da m�sica, do Player e do Volume
		//Par�metro:
		//String music: O nome da m�dia que ser� executada com a sua extens�o, exemplo pokemon.mid
		is = getClass().getResourceAsStream("/"+music); //Pegar� a m�sica indicada que est� no Resource e 
		//como Stream
		try {//Tente
			mc_player = Manager.createPlayer(is,"audio/midi"); //Criar o player, com a m�sica que est� na Stream
			//com o seu tipo que est� em detalhes abaixo:
			/* Mudar o segundo par�metro de acordo com o tipo de m�dia que ser� 
			 * executado
			 * Wave audio Files: audio/x-wav
			 * AU audio Files: audio/basic
			 * MP3 audio Files: audio/mpeg
			 * MIDI Files: audio/midi
			 * Tone Sequences: audio/x-tone-seq
			 */		

			mc_player.setLoopCount(-1); //com o valor -1 a m�sica tocar� infinitamente
			mc_player.start(); //Iniciar� a execu��o da m�sica
			
			vc = (VolumeControl)mc_player.getControl("VolumeControl"); //Pega o controle do Volume desse player
			vc.setLevel(volume); //Inicia o player com o volume atual
			if(volume==0) { 
				vc.setMute(true); //Seta o volume para Mudo caso seja 0, afeta o desempenho (melhorando)
			}
		} catch (IOException e) { //Erro quando n�o existe a m�sica que est� chegando
			System.out.println("IOException createPlayer");
		} catch (MediaException e) { //Erro qualquer relacionado a Midia 
			System.out.println("MediaException createPlayer");
		}
	}

	public void musicVolume(int level){
		//Seta o Volume, j� adaptado para o jogo,
		//Par�metro
		//int Level: Valor do n�vel de volume,Varia de 0 a 100

		if(level==0){
			vc.setMute(true); 	
		}else if (vc.isMuted()){
			vc.setMute(false);
		}
		
		vc.setLevel(level);
	}

	public int musicGetVolume(){ 
		//Pega o volume do Player
		return(vc.getLevel());
	}
}