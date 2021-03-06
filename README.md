                    CE325 Project-4 FILEBROWSER
                    --------------------------
AUTHORS:        Mail:              AEM:  
Sideris George  siderisng@uth.gr   1622
Tsokos Fotis    tsokos@uth.gr      1679


Λειτουργία και δομές δεδομένων
------------------------------
Το πρόγραμμα μας χρησιμοποιεί δύο απλές δεδομένων. Η μία είναι μια δενδρική απεικόνιση
του συστήματος αρχείων και η δεύτερη μια λίστα των αρχείων που περιέχονται σε ένα συγκε-
κριμένο αρχείο. 
Φορτώνοντας όλο το δέντρο σε ένα αρχείο κατά την αρχή του προγράμματος θα έπαιρνε υπερβο-
λικά πολύ χρόνο για αυτό και ακολουθούμε μία πιο δυναμική μορφή ενημέρωσης των κόμβων του
δέντρου. Κάθε φορα που επισκεπτόμαστε ένα φάκελο εισάγουμε στο δέντρο όλους τους υποφακέλους
του μέχρι κάποιο συγκεκριμένο βάθος(Σε εμάς το Default βάθος είναι 3 ώστε να φαίνονται για 
τους κόμβους που έχουν παιδιά η επιλογή tree expansion).
(α)Οι κόμβοι έχουν πάντα το όνομα του φακέλου οπότε μπορούμε να πάρουμε οποιοδήποτε μονοπάτι από
τη ρίζα σε κάποιο κόμβο απλά παίρνοντας τους κόμβους μέχρι να φτάσουμε σε αυτόν και τοποθετώντας
ενδιάμεσα / η \ ανάλογα το λειτουργικό σύστημα.
Η λίστα δεν συνδέεται με οποιονδήποτε τρόπο με το δέντρο. Το δέντρο περιέχει απλούς nodes ενώ η 
λίστα αρχεία. Ωστόσο με τις κατάλληλες μεθόδους πετύχαμε το εξής μοτίβο επικοινωνίας μεταξύ λίστας
δέντρου
1) Όταν κάποιος διαλέγει ένα κόμβο στη δενδρική δομή παίρνουμε με το τρόπο που αναφέραμε στο (α) το
μονοπάτι του και γεμίζουμε τη λίστα με όλα τα αρχεία που περιέχει
2)Όταν κάποιος διαλέγει ένα αρχείο στη λίστα βρίσκουμε το path του και ακολουθώντας την αντίστροφη 
διαδικασία από το (α) βρίσκουμε το node που αντιστοιχεί και το επιλέγουμε στη δενδρική δομή μας.
Ενώ λοιπόν το δέντρο μας είναι μια δυναμική λίστα δεδομένων που αναπτύσσεται με το χρόνο η λίστα
απλώς είναι η επιλογή των αρχείων(ή πιο αναλογικά η εκτύπωση τους) που περιέχονται σε ένα φάκελο
που έχουμε επιλέξει. 
Εφόσων στη λύση μας κυρίως η κύρια εργασία ήταν η διαχείρηση της δενδρικής δομής όλες οι μέθοδοι
για έυρεση, πρόσθεση, αντιγραφή κπλ αναπτύχθηκαν με χρήση αναδρομικών τεχνικών, κυρίως της αναζήτησης
σε βάθος.

Εμφάνιση και χρηστικότητα
-------------------------
Το πρόγραμμα μας χρησιμοποιεί ένα frame το οποίο δημιουργείται μέσω του framebuilder, μέσα στο οποίο δημιουργουμε ενα contentpane, 
μεσα στο οποίο βάζουμε ένα splitpane, το οποίο περιέχει δεξια και αριστερά απο ένα scrollpane τα οποία αναπαριστούν την λίστα των αρχείων
και το δέντρο αντίστοιχα.
Χρησιμοποιούμε ενα textfield και ενα Jbutton για την αναζήτηση,αλλο ενα Jbutton για την προσπέλαση του προηγούμενου φακέλου, ενα menu toolbar
με επιλογές μενού File και Edit για εργασίες με τα αρχεία.
Επίσης, έχουμε ένα popup menu που ενεργοποιείται όταν κάνουμε δεξί κλικ σε ενα αρχείο στην λίστα και εμφανίζει τις διαθέσιμες ενέργειες.
Τα αρχεία εμφανίζονται με horizontal layout μέσω ενός cell renderer που μας επιτρέπει να τα εμφανίζουμε σε grid, με μεγάλα εικονίδια και 
σωστό alignment.Περισσότερες πληροφορίες για αυτά στα σχόλια μέσα στο πρόγραμμα.

Ακόμα, προσθέσαμε εικονίδια σε κάθε menuitem και για καλύτερη χρηστικότητα και εμφάνιση, και χρησιμοποιούμε τα mimetypes για να εμφανίζουμε τα
κατάλληλα εικονίδια για κάθε τύπο αρχείου. Χρησιμοποιούμε InputDialogs και MessageDialogs όπου είναι απαραίτητο.

Όσον αφορά τη λίστα, έχουμε υλοποιήσει τον filebrowser όπως τον windows explorer. Το διπλό κλικ είτε ανοίγει τον φάκελο είτε εκτελεί το αρχείο,
το μονο αριστερό κλικ απλα επιλέγει κατι, το δεξί εμφανίζει το popupmenu. Το κλικ εκτός αρχείων αλλα στο κενό, απο-επιλέγει ότι ήταν επιλεγμένο και
υπάρχει η δυνατότητα να να χρησιμοποιηθούν λειτουργείες οπως το paste και το create οι οποίες εκτελούνται στον γονικό φάκελο όπως και θα έπρεπε.

Τέλος, η αλληλεπίδραση με το χρήστη περιλαμβάνει : ερώτηση για το νέο όνομα του αρχείου πριν την μετονομασία, την δημιουργία αρχείου ή φακέλου, την 
ερώτηση επιβεβαίωσης πριν την διαγραφή και αλλα πολλά όπως η αναζήτηση.


Σημείωσεις:
-----------
1)Το πρόγραμμα αναπτύχθηκε στο IDE NetBeans σε περιβάλλον Windows. Ωστόσο το δοκιμάσαμε σε δύο διαφορετικά
Linux μηχανήματα και έδειχνε να δουλεύει αρκετά καλά.(Δηλαδή δεν παρουσίαζε κάποιο πρόβλημα)
2)Λόγω του τεράστιου όγκου πληροφορίας μερικές φορές το πρόγραμμα ενδέχεται να κολλάει(... απλά χρειάζεται
λίγο υπομονή).
3)Το ίδιο ισχύει και για την αναζήτηση. Όταν αρχίζει η αναζήτηση δίνουμε ένα ενημερωτικό μήνυμα. Ο χρήστης
πρέπει να περιμένει για την εμφάνιση μηνύματος που τον ειδοποιεί ότι τελείωσε η αναζήτηση πριν κάνει οποιαδήποτε 
άλλη ενέργεια.
4)Η αναζήτηση για να γίνει πρέπει να επιλεχθεί κάποιος φάκελος(Για να
 ψάξουμε αρχεία η paths μέσα σε αυτόν) ή
ένα πλήρες path. H αναζήτηση δέχεται κανονικά ονόματα και regular
expressions καθώς και(absolute) paths η (relative)paths με regular
expressions (πχ path/*/a/*/file/*txt). Επισημαίνουμε επίσης οτί 
όλες οι αναζητήσεις είναι της μορφής ^search text$ και ότι για να βρούμε
λέξεις που περιέχουν ένα χαρακτήρα ή λέξη χρησιμοποιούμε regular expressions
5)Εφόσον περάσαμε αρκετές ώρες δουλεύοντας πάνω στο πρόγραμμα αποφασίσαμε ν του δώσουμε ένα
θέμα εισάγοντας αναφορές από την Αλίκη Στη Χώρα Των Θαυμάτων του Lewis Carrol (Προέκυψε από την αναλογία
μεταξύ της εξερεύνησης του explorer και της Aλίκης σε έναν άγνωστο κόσμο).
 Ωστόσο οποιοσδήποτε δεν θέλει
να ακόύσει το κομμάτι που έχουμε ως background music στο πρόγραμμα μπορεί να το απενεργοποιήσει όταν
τον ρωτήσει το αναδυόμενο παράθυρο στην αρχή του προγράμματος.




Προβλήματα:
------------
1)Λόγω κωδικοποίησης του background κομματιού ενδέχεται να μην είναι δυνατή η επεξεργασία του σε κάποια λειτουργικά συστήματα.

