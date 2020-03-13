package transformation.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column
    private Integer size;

    @Column
    private String uploadDate;

    @OneToMany(mappedBy = "batch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Item> itemList;

    public Batch() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}
