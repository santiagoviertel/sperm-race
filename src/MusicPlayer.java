import java.io.IOException;
import java.io.InputStream;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.Manager;
import javax.microedition.media.control.VolumeControl;

public class MusicPlayer {

	private Player mc_player; //Objeto que executará a música
	private VolumeControl vc; //Objeto que Controla o volume
	//private int volume; //Atributo que armazenará o volume 

	public MusicPlayer() { }

	public void musicPause() {
		try { //Tente parar a música
			mc_player.stop();
		} catch (MediaException e) { //Se não der, ignora (geralmente cai aqui quando o 
			//player não está tocando)
		}
	}

	public void musicContinue() {
		try { //Tente iniciar a música (sempre continuará no ponto em que parou) 
			mc_player.start();
		} catch (MediaException e) { //Se não conseguir
			//Falhou...
		}
	}

	public void musicStart(String music,int volume) {
		InputStream is; //Local onde a música estará
		
		if(mc_player!=null){
			
				mc_player.close();
			
		}
    
		//Iniciará a instância da música, do Player e do Volume
		//Parâmetro:
		//String music: O nome da mídia que será executada com a sua extensão, exemplo pokemon.mid
		is = getClass().getResourceAsStream("/"+music); //Pegará a música indicada que está no Resource e 
		//como Stream
		try {//Tente
			mc_player = Manager.createPlayer(is,"audio/midi"); //Criar o player, com a música que está na Stream
			//com o seu tipo que está em detalhes abaixo:
			/* Mudar o segundo parâmetro de acordo com o tipo de mídia que será 
			 * executado
			 * Wave audio Files: audio/x-wav
			 * AU audio Files: audio/basic
			 * MP3 audio Files: audio/mpeg
			 * MIDI Files: audio/midi
			 * Tone Sequences: audio/x-tone-seq
			 */		

			mc_player.setLoopCount(-1); //com o valor -1 a música tocará infinitamente
			mc_player.start(); //Iniciará a execução da música
			
			vc = (VolumeControl)mc_player.getControl("VolumeControl"); //Pega o controle do Volume desse player
			vc.setLevel(volume); //Inicia o player com o volume atual
			if(volume==0) { 
				vc.setMute(true); //Seta o volume para Mudo caso seja 0, afeta o desempenho (melhorando)
			}
		} catch (IOException e) { //Erro quando não existe a música que está chegando
			System.out.println("IOException createPlayer");
		} catch (MediaException e) { //Erro qualquer relacionado a Midia 
			System.out.println("MediaException createPlayer");
		}
	}

	public void musicVolume(int level){
		//Seta o Volume, já adaptado para o jogo,
		//Parâmetro
		//int Level: Valor do nível de volume,Varia de 0 a 100

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