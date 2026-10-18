package com.educa.mathkids.data.repository.problem_bank

data class ProblemEnglishInfo(
    val titleEn: String,
    val storyEn: String,
    val questionEn: String,
    val itemNameEn: String,
    val groupNameEn: String? = null,
    val pedagogicalExplanationEn: String
)

object ProblemTranslationDictionary {

    private val translations: Map<String, ProblemEnglishInfo> = mapOf(
        // ==========================================
        // SUMA - 1 a 10: NIVEL FÁCIL
        // ==========================================
        "suma_1" to ProblemEnglishInfo(
            titleEn = "Problem 1",
            storyEn = "Ana has 2 red apples. Her mom gives her 1 more apple.",
            questionEn = "How many apples does Ana have in total?",
            itemNameEn = "apples",
            pedagogicalExplanationEn = "2 apples plus 1 apple make 3 delicious apples!"
        ),
        "suma_2" to ProblemEnglishInfo(
            titleEn = "Problem 2",
            storyEn = "There are 2 puppies playing on the grass. 1 more puppy arrives to play.",
            questionEn = "What number completes the addition of puppies?",
            itemNameEn = "puppies",
            pedagogicalExplanationEn = "2 puppies and 1 more that arrived are 3 friends playing!"
        ),
        "suma_3" to ProblemEnglishInfo(
            titleEn = "Problem 3",
            storyEn = "Lucas has 1 blue balloon. At the party, he is given 2 more balloons.",
            questionEn = "How many balloons does Lucas have now?",
            itemNameEn = "balloons",
            pedagogicalExplanationEn = "1 balloon plus 2 balloons are 3 balloons flying high!"
        ),
        "suma_4" to ProblemEnglishInfo(
            titleEn = "Problem 4",
            storyEn = "In the fish tank there are 3 fish swimming. 1 more little fish arrives.",
            questionEn = "How many fish swim happily now?",
            itemNameEn = "fish",
            pedagogicalExplanationEn = "3 fish plus 1 little fish make 4 colorful fish!"
        ),
        "suma_5" to ProblemEnglishInfo(
            titleEn = "Problem 5",
            storyEn = "In the garden 2 pink flowers bloom, and today 2 more flowers grow.",
            questionEn = "What number completes the garden flowers?",
            itemNameEn = "flowers",
            pedagogicalExplanationEn = "2 flowers plus 2 flowers make 4 beautiful flowers!"
        ),
        "suma_6" to ProblemEnglishInfo(
            titleEn = "Problem 6",
            storyEn = "Sofia has 2 chocolate cookies. Her grandma gives her 3 more cookies.",
            questionEn = "How many cookies does Sofia have in total?",
            itemNameEn = "cookies",
            pedagogicalExplanationEn = "2 cookies plus 3 cookies make 5 delicious cookies!"
        ),
        "suma_7" to ProblemEnglishInfo(
            titleEn = "Problem 7",
            storyEn = "There is 1 duckling swimming in the pond. 3 more ducklings arrive quacking.",
            questionEn = "How many ducklings are in the pond?",
            itemNameEn = "ducklings",
            pedagogicalExplanationEn = "1 duckling plus 3 ducklings are 4 little yellow ducks!"
        ),
        "suma_8" to ProblemEnglishInfo(
            titleEn = "Problem 8",
            storyEn = "Mate finds 3 bright stars in the sky and discovers 2 more stars.",
            questionEn = "What number completes the stars Mate found?",
            itemNameEn = "stars",
            pedagogicalExplanationEn = "3 stars plus 2 stars are 5 stars shining bright!"
        ),
        "suma_9" to ProblemEnglishInfo(
            titleEn = "Problem 9",
            storyEn = "Tomas has 4 colored pencils. He finds 1 more pencil on his desk.",
            questionEn = "How many pencils does he have for drawing?",
            itemNameEn = "pencils",
            pedagogicalExplanationEn = "4 pencils plus 1 pencil make 5 colorful pencils!"
        ),
        "suma_10" to ProblemEnglishInfo(
            titleEn = "Problem 10",
            storyEn = "There is 1 kitten sleeping under the sun. 4 more kittens come to play.",
            questionEn = "How many kittens are there in total?",
            itemNameEn = "kittens",
            pedagogicalExplanationEn = "1 kitten plus 4 kittens make 5 cute little cats!"
        ),

        // ==========================================
        // SUMA - 11 a 20: NIVEL MEDIO
        // ==========================================
        "suma_11" to ProblemEnglishInfo(
            titleEn = "Problem 11",
            storyEn = "Carlos has 3 sweet candies and his aunt gives him 3 more candies.",
            questionEn = "What number completes Carlos's candies?",
            itemNameEn = "candies",
            pedagogicalExplanationEn = "3 candies plus 3 candies add up to 6 sweet treats!"
        ),
        "suma_12" to ProblemEnglishInfo(
            titleEn = "Problem 12",
            storyEn = "There are 4 butterflies flying in the park. 2 more butterflies join them.",
            questionEn = "How many butterflies are flying together?",
            itemNameEn = "butterflies",
            pedagogicalExplanationEn = "4 butterflies plus 2 butterflies make 6 butterflies in the air!"
        ),
        "suma_13" to ProblemEnglishInfo(
            titleEn = "Problem 13",
            storyEn = "Maria has 5 fresh strawberries and picks 2 more strawberries into the basket.",
            questionEn = "How many strawberries does Maria have in total?",
            itemNameEn = "strawberries",
            pedagogicalExplanationEn = "5 strawberries plus 2 strawberries make 7 delicious fruits!"
        ),
        "suma_14" to ProblemEnglishInfo(
            titleEn = "Problem 14",
            storyEn = "There are 4 bunnies hopping in the field. 3 more bunnies arrive to hop.",
            questionEn = "What number completes the bunnies in the field?",
            itemNameEn = "bunnies",
            pedagogicalExplanationEn = "4 bunnies plus 3 bunnies make 7 hopping bunnies!"
        ),
        "suma_15" to ProblemEnglishInfo(
            titleEn = "Problem 15",
            storyEn = "Leo has 6 toy cars. His cousin gives him 2 more cars.",
            questionEn = "How many toy cars does Leo have in total?",
            itemNameEn = "toy cars",
            pedagogicalExplanationEn = "6 cars plus 2 cars make 8 fast cars!"
        ),
        "suma_16" to ProblemEnglishInfo(
            titleEn = "Problem 16",
            storyEn = "On the table there are 5 cupcakes and mom bakes 3 more cupcakes.",
            questionEn = "How many cupcakes are ready to eat?",
            itemNameEn = "cupcakes",
            pedagogicalExplanationEn = "5 cupcakes plus 3 cupcakes make 8 sweet cupcakes!"
        ),
        "suma_17" to ProblemEnglishInfo(
            titleEn = "Problem 17",
            storyEn = "There are 4 little turtles swimming and 4 more turtles join them in the river.",
            questionEn = "What number completes the swimming turtles?",
            itemNameEn = "turtles",
            pedagogicalExplanationEn = "4 turtles plus 4 turtles make 8 calm turtles!"
        ),
        "suma_18" to ProblemEnglishInfo(
            titleEn = "Problem 18",
            storyEn = "Valentina has 6 balls and finds 3 more balls in the yard.",
            questionEn = "How many balls does Valentina have to play with?",
            itemNameEn = "balls",
            pedagogicalExplanationEn = "6 balls plus 3 balls make 9 bouncy balls!"
        ),
        "suma_19" to ProblemEnglishInfo(
            titleEn = "Problem 19",
            storyEn = "There are 7 birds singing on a branch. 2 more birds arrive.",
            questionEn = "How many birds are singing together?",
            itemNameEn = "birds",
            pedagogicalExplanationEn = "7 birds plus 2 birds make 9 singing birds!"
        ),
        "suma_20" to ProblemEnglishInfo(
            titleEn = "Problem 20",
            storyEn = "Mateo has 5 gold coins in his chest and earns 5 more coins.",
            questionEn = "What number completes the total coins?",
            itemNameEn = "coins",
            pedagogicalExplanationEn = "5 coins plus 5 coins make 10 shiny gold coins!"
        ),

        // ==========================================
        // SUMA - 21 a 30: NIVEL AVANZADO
        // ==========================================
        "suma_21" to ProblemEnglishInfo(
            titleEn = "Problem 21",
            storyEn = "In the box there are 6 juicy apples and we add 4 more apples.",
            questionEn = "How many apples are in the box?",
            itemNameEn = "apples",
            pedagogicalExplanationEn = "6 apples plus 4 apples make 10 apples in total!"
        ),
        "suma_22" to ProblemEnglishInfo(
            titleEn = "Problem 22",
            storyEn = "Mate sees 7 stars in the sky and 4 new stars shine bright.",
            questionEn = "What number completes the stars in the night?",
            itemNameEn = "stars",
            pedagogicalExplanationEn = "7 stars plus 4 stars make 11 shining stars!"
        ),
        "suma_23" to ProblemEnglishInfo(
            titleEn = "Problem 23",
            storyEn = "Camila has 8 sunflowers in her vase and is given 3 more sunflowers.",
            questionEn = "How many sunflowers decorate the house?",
            itemNameEn = "sunflowers",
            pedagogicalExplanationEn = "8 sunflowers plus 3 sunflowers make 11 bright flowers!"
        ),
        "suma_24" to ProblemEnglishInfo(
            titleEn = "Problem 24",
            storyEn = "There are 6 balloons at the entrance and they bring 6 more balloons for the party.",
            questionEn = "What number completes all the balloons?",
            itemNameEn = "balloons",
            pedagogicalExplanationEn = "6 balloons plus 6 balloons make 12 party balloons!"
        ),
        "suma_25" to ProblemEnglishInfo(
            titleEn = "Problem 25",
            storyEn = "Diego has 7 fruit juices and buys 5 more juices for his friends.",
            questionEn = "How many juices does Diego have to share?",
            itemNameEn = "juices",
            pedagogicalExplanationEn = "7 juices plus 5 juices make 12 refreshing drinks!"
        ),
        "suma_26" to ProblemEnglishInfo(
            titleEn = "Problem 26",
            storyEn = "In the basket there are 8 sweet oranges and grandpa brings 4 more oranges.",
            questionEn = "What number completes the oranges in the basket?",
            itemNameEn = "oranges",
            pedagogicalExplanationEn = "8 oranges plus 4 oranges make 12 fresh oranges!"
        ),
        "suma_27" to ProblemEnglishInfo(
            titleEn = "Problem 27",
            storyEn = "There are 9 red ladybugs on a leaf and 4 more ladybugs arrive to rest.",
            questionEn = "How many ladybugs are on the leaf?",
            itemNameEn = "ladybugs",
            pedagogicalExplanationEn = "9 ladybugs plus 4 ladybugs make 13 pretty ladybugs!"
        ),
        "suma_28" to ProblemEnglishInfo(
            titleEn = "Problem 28",
            storyEn = "Lucia has 8 shiny stickers and her brother gives her 5 more stickers.",
            questionEn = "What number completes Lucia's sticker collection?",
            itemNameEn = "stickers",
            pedagogicalExplanationEn = "8 stickers plus 5 stickers make 13 shiny stickers!"
        ),
        "suma_29" to ProblemEnglishInfo(
            titleEn = "Problem 29",
            storyEn = "There are 9 blocks in the tower and we carefully add 5 more blocks.",
            questionEn = "How many blocks does the tall tower have now?",
            itemNameEn = "blocks",
            pedagogicalExplanationEn = "9 blocks plus 5 blocks make 14 building blocks!"
        ),
        "suma_30" to ProblemEnglishInfo(
            titleEn = "Problem 30",
            storyEn = "In the box there are 10 colored crayons and we add 5 new crayons.",
            questionEn = "What number completes the colored crayons?",
            itemNameEn = "crayons",
            pedagogicalExplanationEn = "10 crayons plus 5 crayons make 15 colorful crayons!"
        ),

        // ==========================================
        // RESTA - 1 a 10: NIVEL FÁCIL
        // ==========================================
        "resta_1" to ProblemEnglishInfo(
            titleEn = "Problem 1",
            storyEn = "There were 3 apples in the basket. Ana eats 1 delicious apple.",
            questionEn = "How many apples are left in the basket?",
            itemNameEn = "apples",
            pedagogicalExplanationEn = "We had 3 apples, took away 1, and 2 are left!"
        ),
        "resta_2" to ProblemEnglishInfo(
            titleEn = "Problem 2",
            storyEn = "There were 4 puppies playing. 1 puppy leaves to take a nap.",
            questionEn = "What number completes the puppies that stay playing?",
            itemNameEn = "puppies",
            pedagogicalExplanationEn = "Out of 4 puppies, 1 went to sleep and 3 stayed playing!"
        ),
        "resta_3" to ProblemEnglishInfo(
            titleEn = "Problem 3",
            storyEn = "Lucas had 5 party balloons. The wind blows away 1 balloon.",
            questionEn = "How many balloons does Lucas hold in his hand?",
            itemNameEn = "balloons",
            pedagogicalExplanationEn = "He had 5 balloons, 1 flew away, and 4 stay with Lucas!"
        ),
        "resta_4" to ProblemEnglishInfo(
            titleEn = "Problem 4",
            storyEn = "There are 4 cookies on the plate. Tomas eats 2 cookies with milk.",
            questionEn = "How many cookies are left on the plate?",
            itemNameEn = "cookies",
            pedagogicalExplanationEn = "There were 4 cookies, we took away 2, and 2 remain!"
        ),
        "resta_5" to ProblemEnglishInfo(
            titleEn = "Problem 5",
            storyEn = "You had 3 toy cars and you lend 2 to your best friend to play.",
            questionEn = "What number completes the toy cars you kept?",
            itemNameEn = "toy cars",
            pedagogicalExplanationEn = "You had 3 cars, lent 2, and 1 stays with you!"
        ),
        "resta_6" to ProblemEnglishInfo(
            titleEn = "Problem 6",
            storyEn = "There were 5 birds on a branch. 2 fly away into the sky.",
            questionEn = "How many birds stay singing on the branch?",
            itemNameEn = "birds",
            pedagogicalExplanationEn = "There were 5 birds, 2 flew away, and 3 stay singing!"
        ),
        "resta_7" to ProblemEnglishInfo(
            titleEn = "Problem 7",
            storyEn = "Sofia has 4 beautiful flowers and gives 3 to her mom with love.",
            questionEn = "What number completes the flowers Sofia has left?",
            itemNameEn = "flowers",
            pedagogicalExplanationEn = "She had 4 flowers, gave away 3, and 1 stays with Sofia!"
        ),
        "resta_8" to ProblemEnglishInfo(
            titleEn = "Problem 8",
            storyEn = "There are 5 fish swimming together and 3 swim towards the little plants.",
            questionEn = "How many fish stay in the center?",
            itemNameEn = "fish",
            pedagogicalExplanationEn = "5 fish minus 3 that swam away leaves 2 swimming in the center!"
        ),
        "resta_9" to ProblemEnglishInfo(
            titleEn = "Problem 9",
            storyEn = "There were 5 strawberries in the bowl. You eat 4 delicious strawberries.",
            questionEn = "How many strawberries are left in the bowl?",
            itemNameEn = "strawberries",
            pedagogicalExplanationEn = "There were 5 strawberries, you ate 4, and 1 delicious strawberry is left!"
        ),
        "resta_10" to ProblemEnglishInfo(
            titleEn = "Problem 10",
            storyEn = "Mate has 3 golden stars and uses 1 to cast a magic spell.",
            questionEn = "What number completes the stars Mate kept?",
            itemNameEn = "stars",
            pedagogicalExplanationEn = "Mate had 3 stars, used 1, and keeps 2 magic stars!"
        ),

        // ==========================================
        // RESTA - 11 a 20: NIVEL MEDIO
        // ==========================================
        "resta_11" to ProblemEnglishInfo(
            titleEn = "Problem 11",
            storyEn = "There were 6 cupcakes on the tray. Friends eat 2.",
            questionEn = "How many cupcakes are left on the tray?",
            itemNameEn = "cupcakes",
            pedagogicalExplanationEn = "6 cupcakes minus 2 that were eaten leaves 4 sweet cupcakes!"
        ),
        "resta_12" to ProblemEnglishInfo(
            titleEn = "Problem 12",
            storyEn = "There are 7 candies in the bag and you share 3 candies with your cousin.",
            questionEn = "What number completes the candies in the bag?",
            itemNameEn = "candies",
            pedagogicalExplanationEn = "You had 7 candies, shared 3, and 4 stay in the bag!"
        ),
        "resta_13" to ProblemEnglishInfo(
            titleEn = "Problem 13",
            storyEn = "Leo had 8 balls to play with. 2 roll away into the park.",
            questionEn = "How many balls does Leo keep in his basket?",
            itemNameEn = "balls",
            pedagogicalExplanationEn = "He had 8 balls, 2 rolled away, and 6 stay with Leo!"
        ),
        "resta_14" to ProblemEnglishInfo(
            titleEn = "Problem 14",
            storyEn = "On the tree there were 7 red apples and 4 apples fall to the ground.",
            questionEn = "How many apples stay on the tree?",
            itemNameEn = "apples",
            pedagogicalExplanationEn = "7 apples minus 4 that fell leaves 3 apples on the branches!"
        ),
        "resta_15" to ProblemEnglishInfo(
            titleEn = "Problem 15",
            storyEn = "There are 8 bunnies in the meadow and 3 bunnies go into their burrow.",
            questionEn = "What number completes the bunnies hopping in the meadow?",
            itemNameEn = "bunnies",
            pedagogicalExplanationEn = "8 bunnies minus 3 that left leaves 5 bunnies enjoying the grass!"
        ),
        "resta_16" to ProblemEnglishInfo(
            titleEn = "Problem 16",
            storyEn = "There were 9 balloons in the room. The wind takes 3 balloons out the window.",
            questionEn = "How many balloons stay inside the party room?",
            itemNameEn = "balloons",
            pedagogicalExplanationEn = "9 balloons minus 3 that flew away leaves 6 colorful balloons!"
        ),
        "resta_17" to ProblemEnglishInfo(
            titleEn = "Problem 17",
            storyEn = "Mom baked 10 tasty cookies. The kids eat 4 as a snack.",
            questionEn = "What number completes the remaining cookies?",
            itemNameEn = "cookies",
            pedagogicalExplanationEn = "There were 10 cookies, 4 were eaten, and 6 delicious cookies remain!"
        ),
        "resta_18" to ProblemEnglishInfo(
            titleEn = "Problem 18",
            storyEn = "There were 9 butterflies on the flowers. 4 butterflies fly far away.",
            questionEn = "How many butterflies remain resting on the flowers?",
            itemNameEn = "butterflies",
            pedagogicalExplanationEn = "9 butterflies minus 4 that flew away leaves 5 resting butterflies!"
        ),
        "resta_19" to ProblemEnglishInfo(
            titleEn = "Problem 19",
            storyEn = "Mateo had 8 coins in his piggy bank and uses 5 to buy an ice cream.",
            questionEn = "How many coins are left inside the piggy bank?",
            itemNameEn = "coins",
            pedagogicalExplanationEn = "8 coins minus 5 spent leaves 3 coins in the piggy bank!"
        ),
        "resta_20" to ProblemEnglishInfo(
            titleEn = "Problem 20",
            storyEn = "In the sky there were 10 bright stars. 5 stars hide behind a cloud.",
            questionEn = "What number completes the stars that still shine?",
            itemNameEn = "stars",
            pedagogicalExplanationEn = "10 stars minus 5 hidden leaves 5 stars shining bright!"
        ),

        // ==========================================
        // RESTA - 21 a 30: NIVEL AVANZADO
        // ==========================================
        "resta_21" to ProblemEnglishInfo(
            titleEn = "Problem 21",
            storyEn = "There were 11 pencils on the desk. You put 3 pencils in your pencil case.",
            questionEn = "How many pencils stay on the desk?",
            itemNameEn = "pencils",
            pedagogicalExplanationEn = "11 pencils minus 3 put away leaves 8 pencils on the desk!"
        ),
        "resta_22" to ProblemEnglishInfo(
            titleEn = "Problem 22",
            storyEn = "In the piñata there were 12 little gift packages. 4 packages are handed out.",
            questionEn = "What number completes the gifts left in the piñata?",
            itemNameEn = "gifts",
            pedagogicalExplanationEn = "12 packages minus 4 handed out leaves 8 surprises!"
        ),
        "resta_23" to ProblemEnglishInfo(
            titleEn = "Problem 23",
            storyEn = "There were 13 sunflowers in the field and 5 are picked for vases.",
            questionEn = "How many sunflowers remain standing in the field?",
            itemNameEn = "sunflowers",
            pedagogicalExplanationEn = "13 sunflowers minus 5 picked leaves 8 golden flowers!"
        ),
        "resta_24" to ProblemEnglishInfo(
            titleEn = "Problem 24",
            storyEn = "In the aquarium there were 12 little fish. 5 swim to their coral cave.",
            questionEn = "What number completes the fish swimming out in the open?",
            itemNameEn = "fish",
            pedagogicalExplanationEn = "12 fish minus 5 hidden leaves 7 fish swimming happily!"
        ),
        "resta_25" to ProblemEnglishInfo(
            titleEn = "Problem 25",
            storyEn = "Camila had 14 stickers and gives 6 to her best friend.",
            questionEn = "How many stickers does Camila have left in her album?",
            itemNameEn = "stickers",
            pedagogicalExplanationEn = "14 stickers minus 6 given away leaves 8 shiny stickers!"
        ),
        "resta_26" to ProblemEnglishInfo(
            titleEn = "Problem 26",
            storyEn = "There were 15 strawberries in the basket and the family eats 5 at breakfast.",
            questionEn = "What number completes the strawberries left in the basket?",
            itemNameEn = "strawberries",
            pedagogicalExplanationEn = "15 strawberries minus 5 eaten leaves 10 fresh strawberries!"
        ),
        "resta_27" to ProblemEnglishInfo(
            titleEn = "Problem 27",
            storyEn = "On the farm there were 13 chicks and 4 chicks run to mama hen.",
            questionEn = "How many chicks remain walking in the yard?",
            itemNameEn = "chicks",
            pedagogicalExplanationEn = "13 chicks minus 4 with mom leaves 9 chicks exploring!"
        ),
        "resta_28" to ProblemEnglishInfo(
            titleEn = "Problem 28",
            storyEn = "There were 14 inflated balloons. 5 balloons pop or deflate.",
            questionEn = "What number completes the balloons that stay inflated?",
            itemNameEn = "balloons",
            pedagogicalExplanationEn = "14 balloons minus 5 deflated leaves 9 party balloons!"
        ),
        "resta_29" to ProblemEnglishInfo(
            titleEn = "Problem 29",
            storyEn = "In the pantry there were 15 apples and 6 are used to make pie.",
            questionEn = "How many apples remain in the pantry?",
            itemNameEn = "apples",
            pedagogicalExplanationEn = "15 apples minus 6 used for pie leaves 9 delicious apples!"
        ),
        "resta_30" to ProblemEnglishInfo(
            titleEn = "Problem 30",
            storyEn = "There were 15 cookies in the tin. The cousins eat 7 cookies.",
            questionEn = "What number completes the cookies left in the tin?",
            itemNameEn = "cookies",
            pedagogicalExplanationEn = "15 cookies minus 7 eaten leaves 8 crunchy cookies!"
        ),

        // ==========================================
        // MULTIPLICACIÓN - 1 a 10: NIVEL FÁCIL
        // ==========================================
        "multi_1" to ProblemEnglishInfo(
            titleEn = "Problem 1",
            storyEn = "There are 2 baskets. Each basket has 2 red apples.",
            questionEn = "How many apples are there in total?",
            itemNameEn = "apples",
            groupNameEn = "baskets",
            pedagogicalExplanationEn = "2 groups of 2 apples make 4 apples in total!"
        ),
        "multi_2" to ProblemEnglishInfo(
            titleEn = "Problem 2",
            storyEn = "There are 3 plates on the table. Each plate has 1 cookie.",
            questionEn = "What number completes the multiplication of cookies?",
            itemNameEn = "cookies",
            groupNameEn = "plates",
            pedagogicalExplanationEn = "3 plates with 1 cookie each make 3 yummy cookies!"
        ),
        "multi_3" to ProblemEnglishInfo(
            titleEn = "Problem 3",
            storyEn = "There are 2 gift bags. Each bag has 3 candies.",
            questionEn = "How many candies are there in total?",
            itemNameEn = "candies",
            groupNameEn = "bags",
            pedagogicalExplanationEn = "2 bags with 3 candies each make 6 sweet candies!"
        ),
        "multi_4" to ProblemEnglishInfo(
            titleEn = "Problem 4",
            storyEn = "There are 3 nests in the tree. In each nest 2 little birds sleep.",
            questionEn = "What number completes the birds in the nests?",
            itemNameEn = "birds",
            groupNameEn = "nests",
            pedagogicalExplanationEn = "3 nests times 2 birds each equals 6 little birds!"
        ),
        "multi_5" to ProblemEnglishInfo(
            titleEn = "Problem 5",
            storyEn = "There are 2 toy boxes. Each box has 4 balls.",
            questionEn = "How many balls are there in total?",
            itemNameEn = "balls",
            groupNameEn = "boxes",
            pedagogicalExplanationEn = "2 boxes of 4 balls make 8 balls to play with!"
        ),
        "multi_6" to ProblemEnglishInfo(
            titleEn = "Problem 6",
            storyEn = "There are 4 flower pots on the windowsill. Each pot has 1 flower.",
            questionEn = "What number completes the flowers on the windowsill?",
            itemNameEn = "flowers",
            groupNameEn = "pots",
            pedagogicalExplanationEn = "4 pots with 1 flower each make 4 lovely flowers!"
        ),
        "multi_7" to ProblemEnglishInfo(
            titleEn = "Problem 7",
            storyEn = "There are 2 clear fish tanks. In each tank 3 little fish swim.",
            questionEn = "How many little fish swim in total?",
            itemNameEn = "fish",
            groupNameEn = "tanks",
            pedagogicalExplanationEn = "2 tanks with 3 fish each make 6 swimming fish!"
        ),
        "multi_8" to ProblemEnglishInfo(
            titleEn = "Problem 8",
            storyEn = "There are 3 twigs in the garden. On each twig there are 2 ladybugs.",
            questionEn = "What number completes the ladybugs in the garden?",
            itemNameEn = "ladybugs",
            groupNameEn = "twigs",
            pedagogicalExplanationEn = "3 twigs times 2 ladybugs each equals 6 pretty ladybugs!"
        ),
        "multi_9" to ProblemEnglishInfo(
            titleEn = "Problem 9",
            storyEn = "There are 2 party plates. Each plate has 2 cupcakes.",
            questionEn = "How many cupcakes are ready to celebrate?",
            itemNameEn = "cupcakes",
            groupNameEn = "plates",
            pedagogicalExplanationEn = "2 plates with 2 cupcakes each make 4 party cupcakes!"
        ),
        "multi_10" to ProblemEnglishInfo(
            titleEn = "Problem 10",
            storyEn = "There are 3 balloon packs. Each pack brings 3 colored balloons.",
            questionEn = "What number completes all the balloons?",
            itemNameEn = "balloons",
            groupNameEn = "packs",
            pedagogicalExplanationEn = "3 packs of 3 balloons make 9 party balloons!"
        ),

        // ==========================================
        // MULTIPLICACIÓN - 11 a 20: NIVEL MEDIO
        // ==========================================
        "multi_11" to ProblemEnglishInfo(
            titleEn = "Problem 11",
            storyEn = "There are 4 plates on the table. Each plate has 2 cookies.",
            questionEn = "How many cookies are on the table in total?",
            itemNameEn = "cookies",
            groupNameEn = "plates",
            pedagogicalExplanationEn = "4 plates times 2 cookies each equals 8 delicious cookies!"
        ),
        "multi_12" to ProblemEnglishInfo(
            titleEn = "Problem 12",
            storyEn = "There are 2 shelves in the library. On each shelf there are 5 books.",
            questionEn = "What number completes the books on the shelves?",
            itemNameEn = "books",
            groupNameEn = "shelves",
            pedagogicalExplanationEn = "2 shelves times 5 books each equals 10 adventure books!"
        ),
        "multi_13" to ProblemEnglishInfo(
            titleEn = "Problem 13",
            storyEn = "There are 3 surprise boxes. Each box has 3 toy cars.",
            questionEn = "How many toy cars are in the boxes?",
            itemNameEn = "cars",
            groupNameEn = "boxes",
            pedagogicalExplanationEn = "3 boxes of 3 cars make 9 fast toy cars!"
        ),
        "multi_14" to ProblemEnglishInfo(
            titleEn = "Problem 14",
            storyEn = "There are 2 rows of magic stars. In each row there are 4 stars.",
            questionEn = "What number completes the rows of stars?",
            itemNameEn = "stars",
            groupNameEn = "rows",
            pedagogicalExplanationEn = "2 rows of 4 stars make 8 shining stars!"
        ),
        "multi_15" to ProblemEnglishInfo(
            titleEn = "Problem 15",
            storyEn = "There are 3 fruit baskets. Each basket has 4 strawberries.",
            questionEn = "How many fresh strawberries are there in total?",
            itemNameEn = "strawberries",
            groupNameEn = "baskets",
            pedagogicalExplanationEn = "3 baskets of 4 strawberries make 12 delicious fruits!"
        ),
        "multi_16" to ProblemEnglishInfo(
            titleEn = "Problem 16",
            storyEn = "There are 5 coin pouches. Each pouch has 2 coins.",
            questionEn = "What number completes all the coins?",
            itemNameEn = "coins",
            groupNameEn = "pouches",
            pedagogicalExplanationEn = "5 pouches times 2 coins each equals 10 gold coins!"
        ),
        "multi_17" to ProblemEnglishInfo(
            titleEn = "Problem 17",
            storyEn = "There are 4 tables in the classroom. At each table there are 3 chairs.",
            questionEn = "How many chairs are in the classroom?",
            itemNameEn = "chairs",
            groupNameEn = "tables",
            pedagogicalExplanationEn = "4 tables times 3 chairs each equals 12 classroom chairs!"
        ),
        "multi_18" to ProblemEnglishInfo(
            titleEn = "Problem 18",
            storyEn = "There are 2 crayon boxes. Each box contains 6 crayons.",
            questionEn = "What number completes the total crayons?",
            itemNameEn = "crayons",
            groupNameEn = "boxes",
            pedagogicalExplanationEn = "2 boxes of 6 crayons make 12 drawing crayons!"
        ),
        "multi_19" to ProblemEnglishInfo(
            titleEn = "Problem 19",
            storyEn = "There are 3 trees in the orchard. On each tree hang 4 apples.",
            questionEn = "How many apples are hanging in the orchard?",
            itemNameEn = "apples",
            groupNameEn = "trees",
            pedagogicalExplanationEn = "3 trees times 4 apples each equals 12 juicy apples!"
        ),
        "multi_20" to ProblemEnglishInfo(
            titleEn = "Problem 20",
            storyEn = "There are 5 happy friends and each friend holds 2 inflated balloons.",
            questionEn = "What number completes the friends' balloons?",
            itemNameEn = "balloons",
            groupNameEn = "friends",
            pedagogicalExplanationEn = "5 friends times 2 balloons each equals 10 colorful balloons!"
        ),

        // ==========================================
        // MULTIPLICACIÓN - 21 a 30: NIVEL AVANZADO
        // ==========================================
        "multi_21" to ProblemEnglishInfo(
            titleEn = "Problem 21",
            storyEn = "There are 4 bird nests. In each nest there are 3 light blue eggs.",
            questionEn = "How many little eggs are there in total?",
            itemNameEn = "eggs",
            groupNameEn = "nests",
            pedagogicalExplanationEn = "4 nests times 3 eggs each equals 12 little eggs!"
        ),
        "multi_22" to ProblemEnglishInfo(
            titleEn = "Problem 22",
            storyEn = "There are 3 candy jars in the shop. In each jar there are 5 gummy bears.",
            questionEn = "What number completes all the gummy bears?",
            itemNameEn = "gummy bears",
            groupNameEn = "jars",
            pedagogicalExplanationEn = "3 jars times 5 gummies each equals 15 sweet treats!"
        ),
        "multi_23" to ProblemEnglishInfo(
            titleEn = "Problem 23",
            storyEn = "There are 4 large plates. On each plate there are 4 chocolate chip cookies.",
            questionEn = "How many cookies are ready to enjoy?",
            itemNameEn = "cookies",
            groupNameEn = "plates",
            pedagogicalExplanationEn = "4 plates times 4 cookies each equals 16 cookies!"
        ),
        "multi_24" to ProblemEnglishInfo(
            titleEn = "Problem 24",
            storyEn = "There are 2 party platters. Each platter has 7 cupcakes.",
            questionEn = "What number completes the party cupcakes?",
            itemNameEn = "cupcakes",
            groupNameEn = "platters",
            pedagogicalExplanationEn = "2 platters times 7 cupcakes each equals 14 cupcakes!"
        ),
        "multi_25" to ProblemEnglishInfo(
            titleEn = "Problem 25",
            storyEn = "There are 5 sticker packets. Each packet comes with 3 stickers.",
            questionEn = "How many stickers can you paste in your album?",
            itemNameEn = "stickers",
            groupNameEn = "packets",
            pedagogicalExplanationEn = "5 packets times 3 stickers each equals 15 stickers!"
        ),
        "multi_26" to ProblemEnglishInfo(
            titleEn = "Problem 26",
            storyEn = "There are 4 toy boxes. Each box contains 3 toy cars.",
            questionEn = "What number completes the cars in the toy shop?",
            itemNameEn = "cars",
            groupNameEn = "boxes",
            pedagogicalExplanationEn = "4 boxes times 3 cars each equals 12 fast cars!"
        ),
        "multi_27" to ProblemEnglishInfo(
            titleEn = "Problem 27",
            storyEn = "There are 3 large fish tanks. In each tank live 5 colorful fish.",
            questionEn = "How many colorful fish live in the tanks?",
            itemNameEn = "fish",
            groupNameEn = "tanks",
            pedagogicalExplanationEn = "3 tanks times 5 fish each equals 15 swimming fish!"
        ),
        "multi_28" to ProblemEnglishInfo(
            titleEn = "Problem 28",
            storyEn = "There are 4 shelves in the toy store. On each shelf there are 4 teddy bears.",
            questionEn = "What number completes the teddy bears on display?",
            itemNameEn = "teddy bears",
            groupNameEn = "shelves",
            pedagogicalExplanationEn = "4 shelves times 4 bears each equals 16 soft teddy bears!"
        ),
        "multi_29" to ProblemEnglishInfo(
            titleEn = "Problem 29",
            storyEn = "There are 2 giant pencil cases. In each case there are 8 colored pencils.",
            questionEn = "How many pencils are there for drawing?",
            itemNameEn = "pencils",
            groupNameEn = "cases",
            pedagogicalExplanationEn = "2 cases times 8 pencils each equals 16 colored pencils!"
        ),
        "multi_30" to ProblemEnglishInfo(
            titleEn = "Problem 30",
            storyEn = "There are 5 floral baskets. Each basket has 3 golden sunflowers.",
            questionEn = "What number completes all the sunflowers?",
            itemNameEn = "sunflowers",
            groupNameEn = "baskets",
            pedagogicalExplanationEn = "5 baskets times 3 sunflowers each equals 15 flowers!"
        ),

        // ==========================================
        // DIVISIÓN - 1 a 10: NIVEL FÁCIL
        // ==========================================
        "div_1" to ProblemEnglishInfo(
            titleEn = "Problem 1",
            storyEn = "There are 4 red apples to share equally between 2 friends.",
            questionEn = "How many apples does each friend receive?",
            itemNameEn = "apples",
            pedagogicalExplanationEn = "We share 4 apples between 2 friends: 2 for each one!"
        ),
        "div_2" to ProblemEnglishInfo(
            titleEn = "Problem 2",
            storyEn = "There are 6 yummy cookies to share equally between 2 kids.",
            questionEn = "What number completes the division of cookies?",
            itemNameEn = "cookies",
            pedagogicalExplanationEn = "6 cookies shared between 2 kids gives 3 cookies each!"
        ),
        "div_3" to ProblemEnglishInfo(
            titleEn = "Problem 3",
            storyEn = "There are 6 sweet candies to share equally among 3 friends.",
            questionEn = "How many candies does each friend get?",
            itemNameEn = "candies",
            pedagogicalExplanationEn = "6 candies divided among 3 friends gives 2 sweet candies each!"
        ),
        "div_4" to ProblemEnglishInfo(
            titleEn = "Problem 4",
            storyEn = "There are 2 party balloons for 2 siblings.",
            questionEn = "What number completes the balloons for each sibling?",
            itemNameEn = "balloons",
            pedagogicalExplanationEn = "2 balloons between 2 siblings gives 1 balloon for each!"
        ),
        "div_5" to ProblemEnglishInfo(
            titleEn = "Problem 5",
            storyEn = "There are 4 little balls for 2 puppies to play with equally.",
            questionEn = "How many balls does each puppy get to play with?",
            itemNameEn = "balls",
            pedagogicalExplanationEn = "4 balls between 2 puppies gives 2 balls for each puppy!"
        ),
        "div_6" to ProblemEnglishInfo(
            titleEn = "Problem 6",
            storyEn = "There are 6 fresh strawberries to place equally onto 2 plates.",
            questionEn = "What number completes the strawberries per plate?",
            itemNameEn = "strawberries",
            pedagogicalExplanationEn = "6 strawberries divided onto 2 plates gives 3 on each plate!"
        ),
        "div_7" to ProblemEnglishInfo(
            titleEn = "Problem 7",
            storyEn = "There are 3 lovely flowers to put into 3 vases.",
            questionEn = "How many flowers go into each vase?",
            itemNameEn = "flowers",
            pedagogicalExplanationEn = "3 flowers among 3 vases gives 1 pretty flower per vase!"
        ),
        "div_8" to ProblemEnglishInfo(
            titleEn = "Problem 8",
            storyEn = "There are 4 gold coins to save equally into 2 piggy banks.",
            questionEn = "What number completes the coins in each piggy bank?",
            itemNameEn = "coins",
            pedagogicalExplanationEn = "4 coins into 2 piggy banks gives 2 gold coins each!"
        ),
        "div_9" to ProblemEnglishInfo(
            titleEn = "Problem 9",
            storyEn = "There are 6 colored pencils to share equally among 3 children.",
            questionEn = "How many pencils does each child get to draw with?",
            itemNameEn = "pencils",
            pedagogicalExplanationEn = "6 pencils among 3 children gives 2 pencils to each!"
        ),
        "div_10" to ProblemEnglishInfo(
            titleEn = "Problem 10",
            storyEn = "There are 8 magic stars to share between 2 friends.",
            questionEn = "What number completes the stars for each friend?",
            itemNameEn = "stars",
            pedagogicalExplanationEn = "8 stars between 2 friends gives 4 shining stars each!"
        ),

        // ==========================================
        // DIVISIÓN - 11 a 20: NIVEL MEDIO
        // ==========================================
        "div_11" to ProblemEnglishInfo(
            titleEn = "Problem 11",
            storyEn = "There are 8 cookies to share equally among 4 friends.",
            questionEn = "How many cookies does each friend receive?",
            itemNameEn = "cookies",
            pedagogicalExplanationEn = "8 cookies among 4 friends gives 2 delicious cookies each!"
        ),
        "div_12" to ProblemEnglishInfo(
            titleEn = "Problem 12",
            storyEn = "There are 9 candies to share equally among 3 kids.",
            questionEn = "What number completes the candies for each kid?",
            itemNameEn = "candies",
            pedagogicalExplanationEn = "9 candies divided among 3 kids gives 3 candies each!"
        ),
        "div_13" to ProblemEnglishInfo(
            titleEn = "Problem 13",
            storyEn = "There are 10 red apples to divide equally into 2 baskets.",
            questionEn = "How many apples go into each basket?",
            itemNameEn = "apples",
            pedagogicalExplanationEn = "10 apples divided into 2 baskets gives 5 apples in each!"
        ),
        "div_14" to ProblemEnglishInfo(
            titleEn = "Problem 14",
            storyEn = "There are 8 race cars to divide equally between 2 friends.",
            questionEn = "What number completes the cars for each friend?",
            itemNameEn = "cars",
            pedagogicalExplanationEn = "8 cars divided between 2 friends gives 4 cars to each!"
        ),
        "div_15" to ProblemEnglishInfo(
            titleEn = "Problem 15",
            storyEn = "There are 12 purple grapes to place equally onto 3 plates.",
            questionEn = "How many grapes go onto each plate?",
            itemNameEn = "grapes",
            pedagogicalExplanationEn = "12 grapes divided among 3 plates gives 4 sweet grapes each!"
        ),
        "div_16" to ProblemEnglishInfo(
            titleEn = "Problem 16",
            storyEn = "There are 12 party cupcakes to share among 4 little tables.",
            questionEn = "What number completes the cupcakes per table?",
            itemNameEn = "cupcakes",
            pedagogicalExplanationEn = "12 cupcakes divided among 4 tables gives 3 cupcakes each!"
        ),
        "div_17" to ProblemEnglishInfo(
            titleEn = "Problem 17",
            storyEn = "There are 10 coins to divide equally into 5 pouches.",
            questionEn = "How many coins are kept in each pouch?",
            itemNameEn = "coins",
            pedagogicalExplanationEn = "10 coins into 5 pouches gives 2 coins per pouch!"
        ),
        "div_18" to ProblemEnglishInfo(
            titleEn = "Problem 18",
            storyEn = "There are 9 inflated balloons for 3 classrooms.",
            questionEn = "What number completes the balloons for each classroom?",
            itemNameEn = "balloons",
            pedagogicalExplanationEn = "9 balloons divided among 3 classrooms gives 3 balloons each!"
        ),
        "div_19" to ProblemEnglishInfo(
            titleEn = "Problem 19",
            storyEn = "There are 8 teddy bears to share equally among 4 children.",
            questionEn = "How many teddy bears does each child receive?",
            itemNameEn = "teddy bears",
            pedagogicalExplanationEn = "8 teddy bears among 4 children gives 2 teddy bears each!"
        ),
        "div_20" to ProblemEnglishInfo(
            titleEn = "Problem 20",
            storyEn = "There are 12 pencils to share equally among 3 pencil cases.",
            questionEn = "What number completes the pencils in each case?",
            itemNameEn = "pencils",
            pedagogicalExplanationEn = "12 pencils into 3 cases gives 4 pencils in each!"
        ),

        // ==========================================
        // DIVISIÓN - 21 a 30: NIVEL AVANZADO
        // ==========================================
        "div_21" to ProblemEnglishInfo(
            titleEn = "Problem 21",
            storyEn = "There are 14 ripe strawberries to share between 2 friends.",
            questionEn = "How many strawberries does each friend receive?",
            itemNameEn = "strawberries",
            pedagogicalExplanationEn = "14 strawberries between 2 friends gives 7 strawberries each!"
        ),
        "div_22" to ProblemEnglishInfo(
            titleEn = "Problem 22",
            storyEn = "There are 15 fruit candies to share equally among 3 children.",
            questionEn = "What number completes the candies for each child?",
            itemNameEn = "candies",
            pedagogicalExplanationEn = "15 candies divided among 3 children gives 5 candies each!"
        ),
        "div_23" to ProblemEnglishInfo(
            titleEn = "Problem 23",
            storyEn = "There are 16 crunchy cookies for 4 friends at snack time.",
            questionEn = "How many cookies does each friend get to eat?",
            itemNameEn = "cookies",
            pedagogicalExplanationEn = "16 cookies among 4 friends gives 4 cookies each!"
        ),
        "div_24" to ProblemEnglishInfo(
            titleEn = "Problem 24",
            storyEn = "There are 12 little fish to distribute into 2 large fish tanks.",
            questionEn = "What number completes the fish in each tank?",
            itemNameEn = "fish",
            pedagogicalExplanationEn = "12 fish into 2 tanks gives 6 fish in each tank!"
        ),
        "div_25" to ProblemEnglishInfo(
            titleEn = "Problem 25",
            storyEn = "There are 15 golden stars to share equally among 5 children.",
            questionEn = "How many stars does each child keep?",
            itemNameEn = "stars",
            pedagogicalExplanationEn = "15 stars divided among 5 children gives 3 stars each!"
        ),
        "div_26" to ProblemEnglishInfo(
            titleEn = "Problem 26",
            storyEn = "There are 16 building blocks to build 2 identical towers.",
            questionEn = "What number completes the blocks for each tower?",
            itemNameEn = "blocks",
            pedagogicalExplanationEn = "16 blocks divided into 2 towers gives 8 blocks per tower!"
        ),
        "div_27" to ProblemEnglishInfo(
            titleEn = "Problem 27",
            storyEn = "There are 18 flowers to place equally into 3 vases.",
            questionEn = "How many flowers go into each vase?",
            itemNameEn = "flowers",
            pedagogicalExplanationEn = "18 flowers divided among 3 vases gives 6 flowers each!"
        ),
        "div_28" to ProblemEnglishInfo(
            titleEn = "Problem 28",
            storyEn = "There are 14 balloons to decorate 2 party halls.",
            questionEn = "What number completes the balloons for each hall?",
            itemNameEn = "balloons",
            pedagogicalExplanationEn = "14 balloons divided into 2 party halls gives 7 balloons each!"
        ),
        "div_29" to ProblemEnglishInfo(
            titleEn = "Problem 29",
            storyEn = "There are 15 shiny stickers to divide equally among 3 notebooks.",
            questionEn = "How many stickers can you paste into each notebook?",
            itemNameEn = "stickers",
            pedagogicalExplanationEn = "15 stickers into 3 notebooks gives 5 stickers each!"
        ),
        "div_30" to ProblemEnglishInfo(
            titleEn = "Problem 30",
            storyEn = "There are 16 toy cars to share equally among 4 friends.",
            questionEn = "What number completes the cars for each friend?",
            itemNameEn = "cars",
            pedagogicalExplanationEn = "16 cars divided among 4 friends gives 4 cars each!"
        )
    )

    fun getTranslation(id: String): ProblemEnglishInfo? = translations[id]

    fun translateStory(id: String, fallback: String): String =
        translations[id]?.storyEn ?: fallback

    fun translateQuestion(id: String, fallback: String): String =
        translations[id]?.questionEn ?: fallback

    fun translateItemName(spanishName: String): String = when (spanishName.lowercase()) {
        "manzanas" -> "apples"
        "perritos" -> "puppies"
        "globos" -> "balloons"
        "peces", "pececitos" -> "fish"
        "flores" -> "flowers"
        "galletas" -> "cookies"
        "patitos" -> "ducklings"
        "estrellas" -> "stars"
        "lápices" -> "pencils"
        "gatitos" -> "kittens"
        "caramelos" -> "candies"
        "mariposas" -> "butterflies"
        "fresas" -> "strawberries"
        "conejitos" -> "bunnies"
        "carritos", "autitos" -> "toy cars"
        "pastelitos" -> "cupcakes"
        "tortuguitas" -> "turtles"
        "pelotas", "pelotitas" -> "balls"
        "pajaritos" -> "birds"
        "monedas" -> "coins"
        "girasoles" -> "sunflowers"
        "juguitos" -> "juices"
        "naranjas" -> "oranges"
        "mariquitas" -> "ladybugs"
        "stickers", "pegatinas" -> "stickers"
        "bloques" -> "blocks"
        "crayolas" -> "crayons"
        "pollitos" -> "chicks"
        "libros" -> "books"
        "uvas" -> "grapes"
        "ositos", "ositos de peluche" -> "teddy bears"
        "huevitos" -> "little eggs"
        "gomitas" -> "gummy bears"
        "juguetes" -> "toys"
        else -> spanishName
    }

    fun translateGroupName(spanishGroup: String): String = when (spanishGroup.lowercase()) {
        "canastas" -> "baskets"
        "platos" -> "plates"
        "bolsitas" -> "bags"
        "nidos" -> "nests"
        "cajas" -> "boxes"
        "macetas" -> "pots"
        "peceras" -> "tanks"
        "ramitas" -> "twigs"
        "paquetes" -> "packs"
        "estantes" -> "shelves"
        "filas" -> "rows"
        "mesitas" -> "tables"
        "árboles" -> "trees"
        "amigos" -> "friends"
        "frascos" -> "jars"
        "bandejas" -> "platters"
        "sobres" -> "packets"
        "estuches" -> "cases"
        else -> spanishGroup
    }

    fun translateExplanation(id: String, fallback: String): String =
        translations[id]?.pedagogicalExplanationEn ?: fallback
}
