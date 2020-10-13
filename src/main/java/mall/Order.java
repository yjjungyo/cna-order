package mall;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String productId;
    private Integer qty;
    private String status;

    @PrePersist
    public void onPrePersist(){
        try{
            System.out.println("................ Order . PrePersist ...... sleep .....");
            Thread.currentThread().sleep((long)(800+Math.random()*220));
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }
    @PostUpdate
    //findby 로 검색 후, Save 하면 Update 이벤트가 발생한다. ( Persist 가 아닌 )
    public void onPostUpdate(){
        System.out.println("................Order update event raised by Shipped Event..........!!!");
    }


    @PostPersist
    public void onPostPersist(){
        Ordered ordered = new Ordered();
        BeanUtils.copyProperties(this, ordered);
        ordered.publishAfterCommit();


    }

    @PreRemove
    public void onPreRemove(){
        OrderCancelled orderCancelled = new OrderCancelled();
        BeanUtils.copyProperties(this, orderCancelled);
        orderCancelled.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        mall.external.Cancellation cancellation = new mall.external.Cancellation();

        //coding...
        //cancellation.setId(this.getId());
        cancellation.setOrderId(this.getId());
        cancellation.setStatus("DeliveryCanceled");

        // mappings goes here
        OrderApplication.applicationContext.getBean(mall.external.CancellationService.class)
            .cancel(cancellation);


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
