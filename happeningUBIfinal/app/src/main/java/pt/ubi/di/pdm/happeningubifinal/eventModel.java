package pt.ubi.di.pdm.happeningubifinal;

import android.net.Uri;

public class eventModel {

    private String nome;
    private String descricao;
    private String data;
    private String imagem;
    private String imagemg;
    public String latitude;
    public String longitude;

    private eventModel(){}

    private eventModel(String nome, String descricao, String data, String imagem, String imagemg, String longitude, String latitude){
        this.nome = nome;
        this.descricao = descricao;
        this.data = data;
        this.imagem = imagem;
        this.imagemg = imagemg;
        this.latitude = latitude;
        this.longitude = longitude;

    }



    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImageUrl(String imagem) {
        this.imagem = imagem;
    }

    public String getImagemg() { return imagemg; }

    public void setImagemg(String imagemg) { this.imagemg = imagemg; }


}
