import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class Escalonador {
	
	private List<BCP> filaProntos;
	private List<BCP> filaBloqueados;
	private int quantum;
	
	Escalonador(int quantum) {
		this.filaProntos = new ArrayList<BCP>();
		this.filaBloqueados = new LinkedList<BCP>();
		this.quantum = quantum;
	}
	
	public void novoProcesso(BCP bcp) {
		filaProntos.add(bcp);
	}
	
	public void executar() 
	{
		while (!filaProntos.isEmpty() || !filaBloqueados.isEmpty()) 
		{
			diminuiTempoFilaBloqueio();
			verificaFilaBloqueados();

			if (filaProntos.isEmpty())
				continue;
			
			verificaCreditosFilaProntos();
			
			System.out.println();
			System.out.println("Ordenando fila de prontos");
			ordenaFilaProntos();
			
			imprimeFilas();
			
			BCP maiorPrioridade = filaProntos.get(0);
			
			for (int i = 0; i < quantum; i++) 
			{	
				if (!maiorPrioridade.temCreditos()) {
					System.out.println("Fim dos cr�ditos");
					break;
				}
				
				String comando = maiorPrioridade.executar();
				
				System.out.println();
				System.out.println("Executando comando");
				System.out.println(maiorPrioridade.toStringSimplificado());
				System.out.println(comando);
				
				if (comando.equals("SAIDA")) {
					filaProntos.remove(0);
					break;
				}
				else if (comando.equals("E/S")) 
				{
					maiorPrioridade.bloquear(3);
					filaProntos.remove(0);
					filaBloqueados.add(maiorPrioridade);
					System.out.println("Processo bloqueado");
					break;
				}
				else if (comando.substring(1, 2).equals("="))
				{
					if (comando.substring(0, 1).equals("X"))
						maiorPrioridade.setX(Integer.parseInt(comando.substring(2)));
					else
						maiorPrioridade.setY(Integer.parseInt(comando.substring(2)));
				}
				
				if (i == quantum-1)
					System.out.println("Fim do quantum");
			}
				
			maiorPrioridade.interromper();
		}
	}
	
	private void imprimeFilas() {
		filaProntos.forEach((x) -> System.out.println(x.toString()));
		filaBloqueados.forEach((b) -> System.out.println(b.toString()));
	}
	
	private void diminuiTempoFilaBloqueio() {
		filaBloqueados.forEach((b) -> b.diminuiTempoBloqueio());
	}
	
	private void verificaFilaBloqueados() {
		if (!filaBloqueados.isEmpty() && filaBloqueados.get(0).terminouTempoBloqueio())
			filaProntos.add(filaBloqueados.remove(0));
	}
	
	private void verificaCreditosFilaProntos() {
		if(filaProntos.stream().filter((b) -> b.temCreditos()).count() == 0) {
			System.out.println("Redistribuindo cr�ditos");
			filaProntos.forEach((b) -> b.redistribuirCreditos());
		}
	}
	
	private void ordenaFilaProntos() {
		filaProntos.sort(new Comparator<BCP>() {
			@Override
			public int compare(BCP o1, BCP o2) {
				if (o1.getCreditos() > o2.getCreditos())
					return -1;
				else if (o1.getCreditos() < o2.getCreditos())
					return 1;
				else 
					return 0;
			}
		});
	}
}
