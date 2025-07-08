import java.util.*;

abstract class Book {
    protected String isbn;
    protected String title;
    protected int publishYear;
    protected double price;
    protected String author;

    public Book(String isbn, String title, int publishYear, double price, String author) {
        this.isbn = isbn;
        this.title = title;
        this.publishYear = publishYear;
        this.price = price;
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public double getPrice() {
        return price;
    }

    public abstract boolean isForSale();

    public abstract void processPurchase(int quantity, String email, String address);
}

class PaperBook extends Book {
    private int stock;

    public PaperBook(String isbn, String title, int publishYear, double price, String author, int stock) {
        super(isbn, title, publishYear, price, author);
        this.stock = stock;
    }

    @Override
    public boolean isForSale() {
        return true;
    }

    @Override
    public void processPurchase(int quantity, String email, String address) {
        if (stock < quantity) {
            throw new IllegalArgumentException("Quantum book store: Not enough stock for " + title);
        }
        stock -= quantity;
        ShippingService.send(address);
        System.out.println("Quantum book store: Paper book sent to " + address);
    }
}

class EBook extends Book {
    private String fileType;

    public EBook(String isbn, String title, int publishYear, double price, String author, String fileType) {
        super(isbn, title, publishYear, price, author);
        this.fileType = fileType;
    }

    @Override
    public boolean isForSale() {
        return true;
    }

    @Override
    public void processPurchase(int quantity, String email, String address) {
        if (quantity != 1) {
            throw new IllegalArgumentException("Quantum book store: Only one copy per EBook purchase");
        }
        MailService.send(email);
        System.out.println("Quantum book store: EBook sent to " + email);
    }
}

class ShowcaseBook extends Book {
    public ShowcaseBook(String isbn, String title, int publishYear, double price, String author) {
        super(isbn, title, publishYear, price, author);
    }

    @Override
    public boolean isForSale() {
        return false;
    }

    @Override
    public void processPurchase(int quantity, String email, String address) {
        throw new UnsupportedOperationException("Quantum book store: Showcase book is not for sale.");
    }
}

class Inventory {
    private Map<String, Book> books = new HashMap<>();

    public void addBook(Book book) {
        books.put(book.getIsbn(), book);
        System.out.println("Quantum book store: Book added - " + book.title);
    }

    public List<Book> removeOutdatedBooks(int maxYearsOld, int currentYear) {
        List<Book> removed = new ArrayList<>();
        Iterator<Map.Entry<String, Book>> iterator = books.entrySet().iterator();
        while (iterator.hasNext()) {
            Book book = iterator.next().getValue();
            if (currentYear - book.getPublishYear() > maxYearsOld) {
                removed.add(book);
                iterator.remove();
                System.out.println("Quantum book store: Book removed - " + book.title);
            }
        }
        return removed;
    }

    public double buyBook(String isbn, int quantity, String email, String address) {
        Book book = books.get(isbn);
        if (book == null || !book.isForSale()) {
            throw new IllegalArgumentException("Quantum book store: Book not available or not for sale.");
        }
        book.processPurchase(quantity, email, address);
        double totalPrice = book.getPrice() * quantity;
        System.out.println("Quantum book store: Purchase complete. Total paid: " + totalPrice);
        return totalPrice;
    }
}

class ShippingService {
    public static void send(String address) {
    }
}

class MailService {
    public static void send(String email) {
    }
}

public class QuantumBookstoreFullTest {
    public static void main(String[] args) {
        Inventory inventory = new Inventory();

        Book paperBook = new PaperBook("ISBN001", "Java Basics", 2019, 150.0, "Alice", 10);
        Book ebook = new EBook("ISBN002", "Python for AI", 2021, 100.0, "Bob", "PDF");
        Book showcase = new ShowcaseBook("ISBN003", "Ancient Manuscript", 2000, 0.0, "Unknown");

        inventory.addBook(paperBook);
        inventory.addBook(ebook);
        inventory.addBook(showcase);

        inventory.removeOutdatedBooks(10, 2025);

        try {
            inventory.buyBook("ISBN001", 2, "user@example.com", "123 Cairo St.");
            inventory.buyBook("ISBN002", 1, "user2@example.com", "");
            inventory.buyBook("ISBN003", 1, "user3@example.com", "");
        } catch (Exception e) {
            System.out.println("Quantum book store: Error - " + e.getMessage());
        }
    }
}
