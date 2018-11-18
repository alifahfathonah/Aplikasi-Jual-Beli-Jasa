package app.skripsi.hva.aplikasijualbelijasa.data;

public class GetIklan {
    private String id_jasa;
    private String image;
    private String nama_jasa;
    private String deskripsi;
    private String harga;
    private String kategori;
    private String alamat;
    private String hp_jasa;
    private String username;
    private String nama_akun;
    private String userimage;
    private String id_akun;
    private String tgl_iklan;
    private double latitude, longitude, jarak;

    public GetIklan() {

    }

    public GetIklan(String id_jasa, String image, String nama_jasa, String deskripsi, String kategori,
                    String harga, Double latitude, Double longitude, Double jarak, String alamat,String hp_jasa, String username,
                    String userimage, String nama_akun, String id_akun, String tgl_iklan){
        this.id_jasa = id_jasa;
        this.image = image;
        this.nama_jasa = nama_jasa;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.kategori = kategori;
        this.jarak = jarak;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tgl_iklan = tgl_iklan;
        this.alamat = alamat;
        this.hp_jasa = hp_jasa;
        this.username = username;
        this.nama_akun = nama_akun;
        this.id_akun = id_akun;
        this.userimage = userimage;
    }

    public String getId_jasa() {
        return id_jasa; }
    public void setId_jasa(String id_jasa) {
        this.id_jasa = id_jasa;
    }

    public  String getNama_jasa() { return nama_jasa; }
    public void setNama_jasa(String nama_jasa) {this.nama_jasa = nama_jasa; }

    public  String getImage() { return image; }
    public void setImage(String image) {this.image = image; }

    public String getDeskripsi() {return deskripsi;}
    public void setDeskripsi(String deskripsi) {this.deskripsi = deskripsi;}

    public String getHarga() {return harga;}
    public void setHarga(String harga) {this.harga = harga;}

    public String getKategori() {
        return kategori;
    }
    public void setKategori(String kategori){this.kategori = kategori; }

    public Double getJarak() {return jarak;}
    public void setJarak(Double jarak) {this.jarak = jarak;}

    public String getAlamat() {return alamat;}
    public void setAlamat(String alamat) {this.alamat = alamat;}

    public String getHp_jasa() {
        return hp_jasa;
    }
    public void setHp_jasa(String hp_jasa) { this.hp_jasa = hp_jasa;}

    public Double getLatitude(){
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude(){return longitude;}
    public void setLongitude(Double longitude){this.longitude = longitude;}

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getNama_akun(){
        return nama_akun;
    }
    public void setNama_akun(String nama_akun){
        this.nama_akun = nama_akun;
    }

    public String getId_akun() {return id_akun;}
    public void setId_akun(String id_akun) {
        this.id_akun = id_akun;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getTgl_iklan() {return tgl_iklan;}
    public void setTgl_iklan(String tgl_iklan) {this.tgl_iklan = tgl_iklan;}
}
