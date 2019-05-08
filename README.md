# BrainTrainer
Train your brain by solving the highest number of math problems in 30 seconds.

<h2>Introduction</h2>

- The project is cloud connected using <b>Firebase Realtime database</b> and <b>Firebase Authentication</b>. I take care of resources maintenance to avoid memory leaks and unncessary network requests. for instance: removing realtime db listeners when in no need.
- To use this project, you will need to create your own Firebase key from <a href="console.firebase.google.com">Firebase Console</a>

<h2>Firebase Usage</h2>

- I use only E-mail authentication, and keep track of the registered users in the realtime db.
- The model is pretty simple, I keep track only of user full name, avatar and score

<h2>Project Structure</h2>

- The project consists of 5 activities, I explain what each one contains:
<h4><code>Game Activity</code></h3>

- I use the class <code>QuestionBank</code> to generate random numbers of the math problem and the possible answers using <code>java.util.Random</code>
- A <code>CountDownTimer</code> is used to time 30 seconds for the user, and using it's two methods <code>onTick()</code> and <code>onFinish()</code> where I take care of updating the UI. The <code>CountDownTimer</code> use makes sense here as it also able to communicate with the UI thread.
- After the game ends the user can move into the LeaderBoardActivity where he sees his rank, or he may restart the game.
- The Activity's <b>launchMode</b> is <code>singleTop</code> which keeps this activity instance in the backstack for navigation purposes and also preventing the activity from firing a new <code>CountDownTimer</code> everytime a new instance is created. There could be other ways to handle these issues of course, this is just how I did it.
<h4><code>LeaderBoard Activity</code></h3>

- Leaderboard data is ranked using <b>Comparable</b> and read from <b>firebase realtime db</b> using the <code>SingleValueEventListener</code>, it gets read only once everytime the user starts the activity for resources optmization.
- A <code>RecyclerView</code> is used, each list item shows the user <i>name/avatar/score/gamesCount</i>
- The <b>RecyclerView</b> also has it's <code>setHasFixedSize(true)</code> for optmization(i.e. list item size won't change, no need to request a whole layoutChange) 
<h4><code>Profile Activity</code></h3>

- On successful login, the user is presented with his avatar, top score and a taunt message to keep him going.
- The data is read from the Firebase realtime db, and on leaving the activity, I take care of removing the db listeners.

<h4><code>Home Activity</code></h3>

- FirebaseAuthentication is used to authenticate users using their e-mails. 
- The users may also decide to play offline, but some features won't be available to them.

<h4><code>Registration Activity</code></h3>

- The user enters his credentials and also chooses one of the given avatars.
- I find <b>Firebase Authentication Exception</b> handling very intuitive here, that includes checking for: Internet connection, Weak passwords or Invalid credentials, with a user friendly message pointing where exactly the error is. 
- A <code>ConstraintLayout</code> is used here to group these different components

# Screenshots
<img src="/images/HomeActivity.png" width="250" />        <img src="/images/RegistrationActivity.png" width="250" />
<img src="/images/ProfileActivity.png" width="250" />  <img src="/images/GameActivity.png" width="250" />        <img src="/images/LeaderBoardActivity.png" width="250" />
<img src="/images/ErrorMessage.png" width="250" />

<h3> What more could be done in this project? </h3>

- Adding diffculty modes for harder problems, you will find the QuestionBank class has some constants already defined for it.
- Using other Authentication methods like Google, Facebook
- Allowing users to add avatars of their choice. 
- Adding background music
- Make use of <code>SharedPreferences</code> for offline users to keep track of the top score.
- Localization
