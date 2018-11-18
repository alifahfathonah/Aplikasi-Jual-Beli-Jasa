package app.skripsi.hva.aplikasijualbelijasa.data;

public class DataKategori {
    private String id_kategori;
    private String nama_kategori;
    private String image_kategori;
    private String hint;

    public DataKategori() {
    }

    public DataKategori(String hint, String id, String nama_kategori) {
        this.hint = hint;
        this.id_kategori = id;
        this.nama_kategori = nama_kategori;
    }

    public String getId() { return id_kategori; }

    public void setId(String id) { this.id_kategori = id; }

    public String getKategori() {
        return nama_kategori;
    }

    public void setKategori(String nama_kategori) {
        this.nama_kategori = nama_kategori;
    }

    public String getImage_kategori() {
        return image_kategori;
    }

    public void setImage_kategori(String image_kategori) {
        this.image_kategori = image_kategori;
    }

}
