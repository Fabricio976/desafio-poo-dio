package com.exemplo.entitys;

import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Emprestimo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final int DIAS_EMPRESTIMO = 14;
    public static final double MULTA_POR_DIA = 2.0;

    private final Livro livro;
    private final Usuario usuario;
    private final Date dataEmprestimo;
    private final Date dataDevolucao;
    private double multa;

    public Emprestimo(Livro livro, Usuario usuario) {
        this.livro = livro;
        this.usuario = usuario;
        this.dataEmprestimo = new Date();
        this.dataDevolucao = calcularDataDevolucao();
        this.multa = 0.0;
    }

    public Date calcularDataDevolucao() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataEmprestimo);
        calendar.add(Calendar.DAY_OF_MONTH, DIAS_EMPRESTIMO);
        return calendar.getTime();
    }

    public double calcularMulta() {
        if (new Date().after(dataDevolucao)) {
            long diff = new Date().getTime() - dataDevolucao.getTime();
            long diasAtraso = diff / (24 * 60 * 60 * 1000);
            return diasAtraso * MULTA_POR_DIA;
        }
        return 0.0;
    }

    public Livro getLivro() { return livro; }
    public Usuario getUsuario() { return usuario; }
    public Date getDataDevolucao() { return dataDevolucao; }
    public void setMulta(double multa) { this.multa = multa; }


    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return "Livro: " + livro.getTitulo() + "| Usuário: " + usuario.getNome() +
                "| Empréstimo: " + sdf.format(dataEmprestimo) +
                "| Devolução: " + sdf.format(dataDevolucao) +
                "| Multa: R$ " + String.format("%.2f", multa);
    }
}