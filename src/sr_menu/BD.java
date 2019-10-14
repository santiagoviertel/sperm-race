package SR_Menu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

public class BD {

	public static final short PISTA1 = 80;
	public static final short PISTA2 = 45;
	public static final short PISTA3 = 210;
	public static final short PISTA4 = 70;
	public static final short PISTA5 = 65;

	private RecordStore r;

	public BD(String bd,int caso) {
		try {
			r = RecordStore.openRecordStore(bd,true);
			//Criar dados iniciais
			if(caso==0){
				if(r.getNumRecords()==0) {
					short[] pontos = new short[] {PISTA1,PISTA2,PISTA3,PISTA4,PISTA5};
					for(int i=0;i<pontos.length;i+=1) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						DataOutputStream dos = new DataOutputStream(baos);
						dos.writeShort(pontos[i]);
						dos.flush();
						byte[] sequenciaBytes = baos.toByteArray();
						r.addRecord(sequenciaBytes,0,sequenciaBytes.length);
						dos.close();
						baos.close();
					}
				}
			}else if(caso==1){
				if(r.getNumRecords()==0){
					boolean[] sidequest = {false,false};
					for(int i=0;i<sidequest.length;i++){
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						DataOutputStream dos = new DataOutputStream(baos);
						dos.writeBoolean(sidequest[i]);
						dos.flush();
						byte[] sequenciaBytes = baos.toByteArray();
						r.addRecord(sequenciaBytes,0,sequenciaBytes.length);
						dos.close();
						baos.close();
					}
					short[] opcoes = {4,2};
					for(int i=0;i<opcoes.length;i++){
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						DataOutputStream dos = new DataOutputStream(baos);
						dos.writeShort(opcoes[i]);
						dos.flush();
						byte[] sequenciaBytes = baos.toByteArray();
						r.addRecord(sequenciaBytes,0,sequenciaBytes.length);
						dos.close();
						baos.close();
					}
				}
			}
			//reiniciar(bd);
		} catch(RecordStoreException e) { }
		catch(IOException e) { }
	}

	public short[] leRecordes() {
		int qtd = 0;
		short[] tempos = null;
		try {
			qtd = r.getNumRecords();
			tempos = new short[qtd];
		} catch (RecordStoreNotOpenException e1) { }
		try {
			for(int i=0;i<qtd;i+=1) {
				byte[] sequenciaBytes = r.getRecord(i+1);
				ByteArrayInputStream bais = new ByteArrayInputStream(sequenciaBytes);
				DataInputStream dis = new DataInputStream(bais);
				tempos[i] = dis.readShort();
				dis.close();
				bais.close();
			}
		}
		catch(RecordStoreException e) {
			System.out.println("Erro no Record Store!");
		}
		catch(IOException e) {
			System.out.println("Erro ao comunicar com o Buffer!");
		}
		return tempos;
	}

	public short[] verificarConfiguracoes() {
		short[] sq = new short[4];
		try {
			for(int i=0;i<sq.length;i+=1) {
				byte[] sequenciaBytes = r.getRecord(i+1);
				ByteArrayInputStream bais = new ByteArrayInputStream(sequenciaBytes);
				DataInputStream dis = new DataInputStream(bais);
				if(i<2) {
					if(dis.readBoolean())
						sq[i] = 1;
					else
						sq[i] = 0;
				}
				else
					sq[i] = dis.readShort();
				dis.close();
				bais.close();
			}
		}
		catch(RecordStoreException e) {
			System.out.println("Erro no Record Store SQ!");
		}
		catch(IOException e) {
			System.out.println("Erro ao comunicar com o Buffer SQ!");
		}
		return sq;
	}

	public void editarDados(short[] tempos) {
		try {
			for(int i=0;i<tempos.length;i+=1) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(baos);
				dos.writeShort(tempos[i]);
				dos.flush();
				byte[] sequenciaBytes = baos.toByteArray();
				r.setRecord(i+1,sequenciaBytes,0,sequenciaBytes.length);
				dos.close();
				baos.close();
			}
		}
		catch(RecordStoreException e) {
			System.out.println("Erro no Record Store!");
		}
		catch(IOException e) {
			System.out.println("Erro ao comunicar com o Buffer!");
		}
	}

	public void editarSideQuest(boolean[] sq) {
		try {
			for(int i=0;i<sq.length;i+=1) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(baos);
				dos.writeBoolean(sq[i]);
				dos.flush();
				byte[] sequenciaBytes = baos.toByteArray();
				r.setRecord(i+1,sequenciaBytes,0,sequenciaBytes.length);
				dos.close();
				baos.close();
			}
		}catch(RecordStoreException e) {
			System.out.println("Erro no Record Store Side Quest!");
		}
		catch(IOException e) {
			System.out.println("Erro ao comunicar com o Buffer Side Quest!");
		}
	}

	public void editarOpcoes(short[] op) {
		try {
			for(int i=2;i<op.length;i+=1) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(baos);
				dos.writeShort(op[i]);
				dos.flush();
				byte[] sequenciaBytes = baos.toByteArray();
				r.setRecord(i+1,sequenciaBytes,0,sequenciaBytes.length);
				dos.close();
				baos.close();
			}
		}catch(RecordStoreException e) {
			System.out.println("Erro no Record Store Side Quest!");
		}
		catch(IOException e) {
			System.out.println("Erro ao comunicar com o Buffer Side Quest!");
		}
	}

	public void fechar() {
		try {
			r.closeRecordStore();
		} catch(RecordStoreException e) { }
	}
/*
	public void reiniciar(String x) {
	    try {
            r.closeRecordStore();
            RecordStore.deleteRecordStore(x);
        } catch(RecordStoreException e) { }
	}*/
}