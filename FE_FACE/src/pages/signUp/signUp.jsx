import React from 'react'
import './signUp.css'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faUser } from '@fortawesome/free-solid-svg-icons'
import { faLock } from '@fortawesome/free-solid-svg-icons'
import { faFacebook } from '@fortawesome/free-brands-svg-icons'
import { faGoogle } from '@fortawesome/free-brands-svg-icons'
import { useNavigate } from 'react-router-dom'



const SignUp = () => {
    const navigate = useNavigate();

  return (
    <div>
      <div className="login">
        <div className="login-backgroud">


          <div className="title-login">
            <p>Sign up new accounts</p>
          </div>


          <div className="input">
            <div className="input-acc">
              <FontAwesomeIcon icon={faUser} className='icon' />
              <input type="text" placeholder='User name' />
            </div>
            <div className="input-pass">
              <FontAwesomeIcon icon={faLock} className='icon' />
              <input type="password" placeholder='Password' />
            </div>
            <div className="input-pass">
              <FontAwesomeIcon icon={faLock} className='icon' />
              <input type="password" placeholder='Corfim password' />
            </div>

          </div>

          <div className="button">
            <div className="button-login">
              <button onClick={() => navigate("/login")}>Login</button>
            </div>
            <div className="button-signUp">
              <button>Sign up</button>
            </div>
          </div>
          <div className="or">
            <p>Or</p>
          </div>
          <div className="face-goo">
            <div className="face">
              <button> <a href="https://www.facebook.com/"> <FontAwesomeIcon icon={faFacebook} className='face' /> Sign for Facebook</a> </button>
            </div>
            <div className="google">
              <button> <a href="https://accounts.google.com/v3/signin/identifier?continue=http%3A%2F%2Fsupport.google.com%2Fmail%2Fanswer%2F56256%3Fhl%3Den&ec=GAZAdQ&hl=en&ifkv=AfYwgwWOkqSvh7vhy_TLIerVhd1jStKc9FA4wS_cB0HT9ECa8I3yxOry08V5sCL3Fn8U4BPDPJCE2A&passive=true&sjid=10322847849808881535-NC&flowName=GlifWebSignIn&flowEntry=ServiceLogin&dsh=S121179217%3A1758127040658342"> <FontAwesomeIcon icon={faGoogle} className='google' /> Sign for Google</a> </button>
            </div>
          </div>
           <div className="back">
          <button onClick={() => navigate("/")}>Back to the Webcam</button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default SignUp
