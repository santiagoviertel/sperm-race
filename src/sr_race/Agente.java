package SR_Race;

public class Agente extends Competidor {

	private int tempoVira;
	private int orientacao;

	public Agente(int x,int y,int ang,byte pers,Pista p) {
		super(x,y,ang,pers,p);
		orientacao = ang;
		tempoVira = 0;
	}

	public void atualizaAgente(int deltaTempo,int nivel,Competidor jogador,Inimigos inimigos,float raioVisao) {
		tempoVira += deltaTempo;
		if(tempoVira>100) {
			//Vetor Otimo Local
			float vetorOtimoLocal[] = {retornaPontoCheckpoint()[0]-retornaSprite().getRefPixelX(),retornaPontoCheckpoint()[1]-retornaSprite().getRefPixelY()};
			if(nivel==2)
				atualizaAgenteNivelMedio(vetorOtimoLocal);
			else {
				float vetorOtimoGlobal[];
				//Vetor Otimo Global
				if(distancia(jogador)<raioVisao) {
					vetorOtimoGlobal = new float[2];
					vetorOtimoGlobal[0] = jogador.retornaSprite().getRefPixelX() - retornaSprite().getRefPixelX();
					vetorOtimoGlobal[1] = jogador.retornaSprite().getRefPixelY() - retornaSprite().getRefPixelY();
				}
				else
					vetorOtimoGlobal = null;
				if(nivel==1)
					atualizaAgenteNivelFacil(vetorOtimoLocal,vetorOtimoGlobal,raioVisao);
				else {
					//Vetores Pessimos
					float vetoresPessimo[][] = new float[3][];
					if(inimigos!=null) {
						float vet1[] = new float[2];
						float vet2[] = new float[2];
						float vet3[] = new float[2];	
						vet1[0] = retornaSprite().getRefPixelX() - inimigos.retornaDiafragma().getRefPixelX();
						vet1[1] = retornaSprite().getRefPixelY() - inimigos.retornaDiafragma().getRefPixelY();
						vet2[0] = retornaSprite().getRefPixelX() - inimigos.retornaExterminador().getRefPixelX();
						vet2[1] = retornaSprite().getRefPixelY() - inimigos.retornaExterminador().getRefPixelY();
						vet3[0] = retornaSprite().getRefPixelX() - inimigos.retornaEspermicida().getX() - (inimigos.retornaEspermicida().getWidth()/2);
						vet3[1] = retornaSprite().getRefPixelY() - inimigos.retornaEspermicida().getY() - (inimigos.retornaEspermicida().getHeight()/2);

						float modulo = vet1[0]*vet1[0] + vet1[1]*vet1[1];
						if(modulo<raioVisao) {
							vetoresPessimo[0] = new float[2];
							vetoresPessimo[0][0] = vet1[0];
							vetoresPessimo[0][1] = vet1[1];
						}
						else
							vetoresPessimo[0] = null;

						if(inimigos.retornaExterminador().isVisible()) {
							modulo = vet2[0]*vet2[0] + vet2[1]*vet2[1];
							if(modulo<raioVisao) {
								vetoresPessimo[1] = new float[2];
								vetoresPessimo[1][0] = vet2[0];
								vetoresPessimo[1][1] = vet2[1];
							}
							else
								vetoresPessimo[1] = null;
						}
						else
							vetoresPessimo[1] = null;

						if(inimigos.retornaEspermicida().isVisible()) {
							modulo = vet3[0]*vet3[0] + vet3[1]*vet3[1];
							if(modulo<raioVisao) {
								vetoresPessimo[2] = new float[2];
								vetoresPessimo[2][0] = vet3[0];
								vetoresPessimo[2][1] = vet3[1];
							}
							else
								vetoresPessimo[2] = null;
						}
						else
							vetoresPessimo[2] = null;
					}
					else {
						vetoresPessimo[0] = null;
						vetoresPessimo[1] = null;
						vetoresPessimo[2] = null;
					}
					atualizaAgenteNivelDificil(vetorOtimoLocal,vetorOtimoGlobal,vetoresPessimo,raioVisao);
				}
			}
		}
	}

	private void atualizaAgenteNivelDificil(float[] vetorOtimoLocal,float[] vetorOtimoGlobal,float[][] vetoresPessimo,float raioVisao) {
		float modulo;
		float pesoVol = 1.0f;
		float denominador = 0.0f;
		if(vetorOtimoGlobal!=null) {
			pesoVol = 0.5f;
			modulo = (float)Math.sqrt(vetorOtimoGlobal[0]*vetorOtimoGlobal[0] + vetorOtimoGlobal[1]*vetorOtimoGlobal[1]);
			if(modulo!=0) {
				vetorOtimoGlobal[0] /= modulo;
				vetorOtimoGlobal[1] /= modulo;
			}
		}
		float pesoVp[] = new float[vetoresPessimo.length];
		int i;
		for(i=0;i<vetoresPessimo.length;i+=1)
			if(vetoresPessimo[i]!=null) {
				pesoVol = 0.5f;
				modulo = vetoresPessimo[i][0]*vetoresPessimo[i][0] + vetoresPessimo[i][1]*vetoresPessimo[i][1];
				pesoVp[i] = (raioVisao - modulo)/raioVisao;
				modulo = (float)Math.sqrt(modulo);
				if(modulo!=0) {
					vetoresPessimo[i][0] /= modulo;
					vetoresPessimo[i][1] /= modulo;
				}
				denominador += pesoVp[i];
			}
		float vetor[] = {0.0f,0.0f};
		if(pesoVol!=1) {
			modulo = (float)Math.sqrt(vetorOtimoLocal[0]*vetorOtimoLocal[0] + vetorOtimoLocal[1]*vetorOtimoLocal[1]);
			if(modulo!=0) {
				vetorOtimoLocal[0] /= modulo;
				vetorOtimoLocal[1] /= modulo;
			}
			if(modulo<retornaDistanciaMaximaPontoCentral())
				pesoVol = modulo/retornaDistanciaMaximaPontoCentral();
			else
				pesoVol = 1.0f;
			denominador += pesoVol;
			if(vetorOtimoGlobal!=null) {
				float pesoVog = 1.0f - pesoVol;
				denominador += pesoVog;
				pesoVog /= denominador + denominador;
				vetor[0] += pesoVog*vetorOtimoGlobal[0];
				vetor[1] += pesoVog*vetorOtimoGlobal[1];
				//System.out.println("PVOG: "+pesoVog);
			}
			denominador += denominador;
			for(i=0;i<pesoVp.length;i+=1)
				if(vetoresPessimo[i]!=null) {
					vetor[0] += (pesoVp[i]*vetoresPessimo[i][0])/denominador;
					vetor[1] += (pesoVp[i]*vetoresPessimo[i][1])/denominador;
					//System.out.println("PVP["+i+"]: "+(pesoVp[i]/denominador));
				}
			pesoVol /= denominador;
			pesoVol += 0.5f;
			//System.out.println("PVOL: "+pesoVol+"\n_________________________________");
			vetor[0] += pesoVol*vetorOtimoLocal[0];
			vetor[1] += pesoVol*vetorOtimoLocal[1];
		}
		else {
			vetor[0] = vetorOtimoLocal[0];
			vetor[1] = vetorOtimoLocal[1];
		}

		atualizaOrientacao(vetor);
	}

	private void atualizaAgenteNivelMedio(float[] vetorOtimoLocal) {
		atualizaOrientacao(vetorOtimoLocal);
	}

	private void atualizaAgenteNivelFacil(float[] vetorOtimoLocal,float[] vetorOtimoGlobal,float raioVisao) {
		if(vetorOtimoGlobal!=null) {
			float modulo = vetorOtimoGlobal[0]*vetorOtimoGlobal[0] + vetorOtimoGlobal[1]*vetorOtimoGlobal[1];
			float pesoVog = 0.45f*((raioVisao - modulo)/raioVisao);
			float pesoVol = 1.0f - pesoVog;
			modulo = (float)Math.sqrt(modulo);
			vetorOtimoGlobal[0] = (0.0f - vetorOtimoGlobal[0])/modulo;
			vetorOtimoGlobal[1] = (0.0f - vetorOtimoGlobal[1])/modulo;
			modulo = (float)Math.sqrt(vetorOtimoLocal[0]*vetorOtimoLocal[0] + vetorOtimoLocal[1]*vetorOtimoLocal[1]);
			vetorOtimoLocal[0] /= modulo;
			vetorOtimoLocal[1] /= modulo;
			float vetor[] = new float[2];
			vetor[0] = pesoVol*vetorOtimoLocal[0] + pesoVog*vetorOtimoGlobal[0];
			vetor[1] = pesoVol*vetorOtimoLocal[1] + pesoVog*vetorOtimoGlobal[1];
			atualizaOrientacao(vetor);
		}
		else
			atualizaOrientacao(vetorOtimoLocal);
	}

	private void atualizaOrientacao(float[] vetor) {
		float modulo = (float)Math.sqrt(vetor[0]*vetor[0] + vetor[1]*vetor[1]);
		if(modulo!=0) {
			vetor[0] /= modulo;
			vetor[1] /= modulo;
			float[] or = retornaOrientacao();
			float cosAlfa = or[0]*vetor[0] + or[1]*vetor[1];
			if(cosAlfa<Math.cos(Math.PI/16)) {
				if(vetor[0]!=0) {
					float tan = vetor[1]/vetor[0];
					if(tan>=0) {
						if(vetor[0]>=0) {
							if(or[1]>tan*or[0])
								orientacao += 1;
							else
								orientacao -= 1;
						}
						else {
							if(or[1]>tan*or[0])
								orientacao -= 1;
							else
								orientacao += 1;
						}
					}
					else {
						if(vetor[0]>0) {
							if(or[1]>tan*or[0])
								orientacao += 1;
							else
								orientacao -= 1;
						}
						else {
							if(or[1]>tan*or[0])
								orientacao -= 1;
							else
								orientacao += 1;
						}
					}
				}
				else {
					if(or[0]<0)
						orientacao += 1;
					else
						orientacao -= 1;
				}
				if(orientacao==16)
					orientacao = 0;
				else if(orientacao==-1)
					orientacao = 15;
				mudaOrientacao(orientacao);
				tempoVira = 0;
			}
		}
	}

	private float distancia(Competidor c) {
		int x = c.retornaSprite().getRefPixelX() - retornaSprite().getRefPixelX(),y = c.retornaSprite().getRefPixelY() - retornaSprite().getRefPixelY();
		return x*x + y*y;
	}
	
	
}